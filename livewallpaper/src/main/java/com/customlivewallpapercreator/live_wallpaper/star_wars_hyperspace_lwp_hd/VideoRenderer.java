package com.customlivewallpapercreator.live_wallpaper.star_wars_hyperspace_lwp_hd;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.view.MotionEvent;

import org.rajawali3d.cameras.Camera2D;
import org.rajawali3d.materials.Material;
import org.rajawali3d.materials.textures.ATexture;
import org.rajawali3d.materials.textures.StreamingTexture;
import org.rajawali3d.primitives.Plane;
import org.rajawali3d.renderer.Renderer;

public class VideoRenderer extends Renderer {

    private final int RENDER_MODE_STRETCH_TO_SCREEN = 0;
    private final int RENDER_MODE_FIT_TO_SCREEN = 1;
    private final int RENDER_MODE_NONE = 2;

    private final int RENDER_MODE = RENDER_MODE_STRETCH_TO_SCREEN;

    private Context mContext;
    private DisplayHelper displayHelper;

    private Plane mScreen;
    private MediaPlayer mMediaPlayer;
    private StreamingTexture mVideoTexture;

    public VideoRenderer(Context context) {
        super(context);

        mContext = context;
        displayHelper = new DisplayHelper(mContext, DisplayHelper.DISPLAY_ORIENTATION_LOCK_AUTO_ROTATE);
    }

    @Override
    protected void initScene() {
        Camera2D camera2D = new Camera2D();
        getCurrentScene().addAndSwitchCamera(camera2D);

        mMediaPlayer = MediaPlayer.create(mContext, R.raw.video);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(0.0f, 0.0f);

        mVideoTexture = new StreamingTexture("video", mMediaPlayer);
        Material mMaterial = new Material();
        mMaterial.setColorInfluence(0);
        try {
            mMaterial.addTexture(mVideoTexture);
        } catch (ATexture.TextureException e) {}

        PointF screenDimension = this.getScreenDimensionsForVideo(RENDER_MODE);
        mScreen = new Plane(screenDimension.x, screenDimension.y, 1, 1);

        mScreen.setMaterial(mMaterial);
        getCurrentScene().addChild(mScreen);

        mMediaPlayer.start();
    }

    private PointF getScreenDimensionsForVideo(int renderMode) {
        float ratioWidth, ratioHeight;

        switch (renderMode) {
            case RENDER_MODE_FIT_TO_SCREEN:
                // Calculate the scaling ratios of the video
                ratioWidth = (float) mMediaPlayer.getVideoWidth() / (float) displayHelper.getWidth();
                ratioHeight = (float) mMediaPlayer.getVideoHeight() / (float) displayHelper.getHeight();

                // Pick the smallest ratio, to ensure our video fits within the screen resolution
                float ratio = (ratioWidth < ratioHeight) ? ratioWidth : ratioHeight;

                // Work out the new resolution for the video using the smallest scaling ratio
                float newWidth = (float) mMediaPlayer.getVideoWidth() * ratio;
                float newHeight = (float) mMediaPlayer.getVideoHeight() * ratio;

                // Recalculate the scaling ratios of the new video resolutions against the screen resolution
                ratioWidth = newWidth / (float) displayHelper.getWidth();
                ratioHeight = newHeight / (float) displayHelper.getHeight();
                break;
            case RENDER_MODE_NONE:
                ratioWidth = (float) mMediaPlayer.getVideoWidth() / displayHelper.getWidth();
                ratioHeight = (float) mMediaPlayer.getVideoHeight() / displayHelper.getHeight();
                break;
            default:
            case RENDER_MODE_STRETCH_TO_SCREEN:
                ratioWidth = 1f;
                ratioHeight = 1f;
                break;
        }

        return new PointF(ratioWidth, ratioHeight);
    }

    @Override
    protected void onRender(long ellapsedRealtime, double deltaTime) {
        super.onRender(ellapsedRealtime, deltaTime);

        if (mVideoTexture instanceof StreamingTexture) {
            mVideoTexture.update();
        }
    }

    @Override
    public void onPause() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.pause();
            } catch (IllegalStateException ex) {}
        }

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        displayHelper.refresh();

        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void onRenderSurfaceDestroyed(SurfaceTexture surfaceTexture) {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
            } catch (IllegalStateException ex) {}

            mMediaPlayer.release();
        }

        super.onRenderSurfaceDestroyed(surfaceTexture);
    }

    @Override
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {}

    @Override
    public void onTouchEvent(MotionEvent event) {}

}
