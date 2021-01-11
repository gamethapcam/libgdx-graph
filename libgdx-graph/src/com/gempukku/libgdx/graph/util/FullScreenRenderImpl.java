package com.gempukku.libgdx.graph.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexData;
import com.badlogic.gdx.utils.Disposable;
import com.gempukku.libgdx.graph.pipeline.loader.FullScreenRender;

public class FullScreenRenderImpl implements FullScreenRender, Disposable {
    private VertexData vertexData;
    private IndexData indexData;

    public FullScreenRenderImpl() {
        float[] verticeData = new float[]{
                0, 0, 0,
                0, 1, 0,
                1, 0, 0,
                1, 1, 0};
        short[] indices = {0, 2, 1, 2, 3, 1};

        vertexData = GdxCompatibilityUtils.createVertexBuffer(true, 4, new VertexAttributes(VertexAttribute.Position()));
        indexData = new IndexBufferObject(true, indices.length);
        vertexData.setVertices(verticeData, 0, verticeData.length);
        indexData.setIndices(indices, 0, indices.length);
    }

    @Override
    public void renderFullScreen(ShaderProgram shaderProgram) {
        vertexData.bind(shaderProgram);
        indexData.bind();
        Gdx.gl20.glDrawElements(Gdx.gl20.GL_TRIANGLES, indexData.getNumIndices(), GL20.GL_UNSIGNED_SHORT, 0);
        vertexData.unbind(shaderProgram);
        indexData.unbind();
    }

    @Override
    public void dispose() {
        vertexData.dispose();
        indexData.dispose();
    }
}
