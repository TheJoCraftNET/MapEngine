package de.pianoman911.mapengine.api.util;

import org.jetbrains.annotations.ApiStatus;

public enum Alignment {

    /**
     * <ul>
     *     <li>Left for x-alignment</li>
     *     <li>Top for y-alignment</li>
     * </ul>
     */
    START {
        @Override
        public int getOffset(int width) {
            return 0;
        }
    },

    /**
     * <ul>
     *     <li>Center for x-alignment</li>
     *     <li>Center for y-alignment</li>
     * </ul>
     */
    CENTER {
        @Override
        public int getOffset(int width) {
            return width / -2;
        }
    },

    /**
     * <ul>
     *     <li>Right for x-alignment</li>
     *     <li>Bottom for y-alignment</li>
     * </ul>
     */
    END {
        @Override
        public int getOffset(int width) {
            return -width;
        }
    };

    @ApiStatus.Internal
    public abstract int getOffset(int width);
}
