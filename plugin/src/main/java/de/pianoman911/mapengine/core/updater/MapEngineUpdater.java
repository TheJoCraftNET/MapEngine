package de.pianoman911.mapengine.core.updater;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.papermc.paper.util.JarManifests;
import io.papermc.paper.util.Tick;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.jar.Manifest;

public final class MapEngineUpdater implements Listener {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();
    private static final Component PREFIX = Component.text()
            .append(Component.text('[', NamedTextColor.GRAY))
            .append(Component.text("MapEngine", NamedTextColor.GOLD))
            .append(Component.text(']', NamedTextColor.GRAY))
            .appendSpace().build();
    private static final String GITHUB_REPO = "TheJoCraftNET/MapEngine";

    private final Set<UUID> notifiedPlayers = new HashSet<>();
    private final Plugin plugin;

    private UpdateState state = UpdateState.NOT_CHECKED;
    private String downloadUrl = null;
    private String updateInfo = null;

    public MapEngineUpdater(Plugin plugin) {
        this.plugin = plugin;

        long period = Tick.tick().fromDuration(Duration.ofHours(plugin.getConfig().getLong("updater.interval-hours", 24L)));
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::checkForUpdate, 0, period);
    }

    private void checkForUpdate() {
        boolean notifyPlayers = true;

        try {
            this.plugin.getLogger().info("Checking for updates...");
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.github.com/repos/" + GITHUB_REPO + "/releases")).build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                this.plugin.getLogger().warning("Failed to check for updates: Received " + response.statusCode());
                this.state = UpdateState.UPDATE_REQUEST_FAILED;
                return;
            }

            JsonArray versions = GSON.fromJson(response.body(), JsonArray.class);
            if (versions.isEmpty()) {
                this.plugin.getLogger().warning("Failed to check for updates: No versions found");
                this.state = UpdateState.UPDATE_REQUEST_FAILED;
                return;
            }

            Manifest manifest = JarManifests.manifest(MapEngineUpdater.class);
            Objects.requireNonNull(manifest, "Corrupt jarfile: No manifest found");

            JsonObject latestRelease = versions.get(0).getAsJsonObject();
            String latestTag = latestRelease.get("tag_name").getAsString();
            String currentTag = manifest.getMainAttributes().getValue("Git-Tag");

            if (currentTag.equals(latestTag)) {
                this.plugin.getLogger().info("MapEngine version " + currentTag + " is up-to-date :)");
                this.state = UpdateState.UP_TO_DATE;
                notifyPlayers = false;
            } else {
                String updateInfo = currentTag + " -> " + latestTag;
                if (Objects.equals(this.updateInfo, updateInfo)) {
                    notifyPlayers = false;
                }
                this.updateInfo = updateInfo;

                this.plugin.getLogger().info("Update found: " + updateInfo);
                this.downloadUrl = latestRelease.get("html_url").getAsString();
                this.state = UpdateState.AVAILABLE;
            }
        } catch (Throwable throwable) {
            this.plugin.getSLF4JLogger().warn("Please report the following error to the developer:", throwable);
            this.state = UpdateState.UPDATE_REQUEST_FAILED;
        } finally {
            if (notifyPlayers) {
                this.notifiedPlayers.clear();
                this.notifyPlayers();
            }
        }
    }

    private void notifyPlayer(Player player) {
        if (!this.notifiedPlayers.add(player.getUniqueId())) {
            return;
        }

        Component message = switch (this.state) {
            case UPDATE_REQUEST_FAILED -> Component.text(
                    "Failed to check for updates, please check the log for more info", NamedTextColor.RED);
            case AVAILABLE -> Component.text()
                    .content("Update available").color(NamedTextColor.YELLOW)
                    .appendSpace()
                    .append(Component.text('(', NamedTextColor.GRAY))
                    .append(Component.text(this.updateInfo, NamedTextColor.GREEN, TextDecoration.UNDERLINED)
                            .hoverEvent(Component.text(this.downloadUrl, NamedTextColor.GRAY))
                            .clickEvent(ClickEvent.openUrl(this.downloadUrl)))
                    .append(Component.text(')', NamedTextColor.GRAY))
                    .build();
            default -> null;
        };

        if (message != null) {
            player.sendMessage(PREFIX.append(message));
        }
    }

    private void notifyPlayers() {
        if (!this.plugin.getConfig().getBoolean("updater.notify-admins", true)) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("mapengine.update-notify")) {
                this.notifyPlayer(player);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!this.plugin.getConfig().getBoolean("updater.notify-admins", true)) {
            return;
        }

        if (event.getPlayer().hasPermission("mapengine.update-notify")) {
            this.notifyPlayer(event.getPlayer());
        }
    }

    private enum UpdateState {

        NOT_CHECKED,
        UPDATE_REQUEST_FAILED,
        UP_TO_DATE,
        AVAILABLE,
    }
}
