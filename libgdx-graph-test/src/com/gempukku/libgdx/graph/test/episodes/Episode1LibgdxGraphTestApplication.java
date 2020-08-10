package com.gempukku.libgdx.graph.test.episodes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.gempukku.libgdx.graph.PipelineLoader;
import com.gempukku.libgdx.graph.renderer.PipelineRenderer;
import com.gempukku.libgdx.graph.renderer.RenderOutputs;
import com.gempukku.libgdx.graph.renderer.RendererLoaderCallback;

import java.io.IOException;
import java.io.InputStream;

public class Episode1LibgdxGraphTestApplication extends ApplicationAdapter {
    private long lastProcessedInput;

    private PipelineRenderer pipelineRenderer;

    @Override
    public void create() {
        pipelineRenderer = loadPipelineRenderer();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        reloadRendererIfNeeded();

        pipelineRenderer.render(delta, RenderOutputs.drawToScreen);
    }

    private void reloadRendererIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (lastProcessedInput + 200 < currentTime) {
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                lastProcessedInput = currentTime;
                pipelineRenderer.dispose();
                pipelineRenderer = loadPipelineRenderer();
            }
        }
    }

    @Override
    public void dispose() {
        pipelineRenderer.dispose();
    }

    private PipelineRenderer loadPipelineRenderer() {
        try {
            InputStream stream = Gdx.files.local("episodes/episode1.json").read();
            try {
                PipelineRenderer pipelineRenderer = PipelineLoader.loadPipeline(stream, new RendererLoaderCallback());
                setupPipeline(pipelineRenderer);
                return pipelineRenderer;
            } finally {
                stream.close();
            }
        } catch (IOException exp) {
            throw new RuntimeException("Unable to load pipeline", exp);
        }
    }

    private void setupPipeline(PipelineRenderer pipelineRenderer) {

    }
}