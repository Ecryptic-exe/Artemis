/*
 * Copyright © Wynntils 2024.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.overlays;

import com.wynntils.core.consumers.overlays.OverlayPosition;
import com.wynntils.core.consumers.overlays.OverlaySize;
import com.wynntils.core.consumers.overlays.TextOverlay;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.VerticalAlignment;

public class ServerUptimeInfoOverlay extends TextOverlay {
    public ServerUptimeInfoOverlay() {
        super(
                new OverlayPosition(
                        160 + McUtils.mc().font.lineHeight,
                        6,
                        VerticalAlignment.TOP,
                        HorizontalAlignment.LEFT,
                        OverlayPosition.AnchorSection.TOP_LEFT),
                new OverlaySize(130, 60),
                HorizontalAlignment.LEFT,
                VerticalAlignment.MIDDLE);
    }

    @Override
    protected String getTemplate() {
        return "§7Your World: §b({current_world}) §e{current_world_uptime}\n§7Newest World: §b({newest_world}) §e{world_uptime(newest_world)}";
    }

    @Override
    protected String getPreviewTemplate() {
        return "§7Your World: §b(WC1) §e6:32\n§7Newest World: §b(WC12) §e0:21";
    }

    @Override
    public boolean isRenderedDefault() {
        return McUtils.mc().gui.getTabList().visible;
    }
}
