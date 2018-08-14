package com.customlivewallpapercreator.live_wallpaper.star_wars_hyperspace_lwp_hd;

import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.wallpaper.Wallpaper;

public class CustomWallpaper extends Wallpaper {

	@Override
	public Engine onCreateEngine() {
		Renderer mRenderer = new VideoRenderer(this);
		return new WallpaperEngine(getBaseContext(), mRenderer);
	}

}
