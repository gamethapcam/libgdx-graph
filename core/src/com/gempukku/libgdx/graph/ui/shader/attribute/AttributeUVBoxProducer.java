package com.gempukku.libgdx.graph.ui.shader.attribute;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gempukku.libgdx.graph.NodeConfiguration;
import com.gempukku.libgdx.graph.data.GraphNodeInput;
import com.gempukku.libgdx.graph.data.GraphNodeOutput;
import com.gempukku.libgdx.graph.shader.ShaderFieldType;
import com.gempukku.libgdx.graph.shader.config.attribute.AttributeUVShaderNodeConfiguration;
import com.gempukku.libgdx.graph.ui.graph.GraphBox;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxImpl;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxInputConnector;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxOutputConnector;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxPart;
import com.gempukku.libgdx.graph.ui.producer.GraphBoxProducer;
import org.json.simple.JSONObject;

import java.util.Iterator;

public class AttributeUVBoxProducer implements GraphBoxProducer<ShaderFieldType> {
    private NodeConfiguration<ShaderFieldType> configuration = new AttributeUVShaderNodeConfiguration();

    @Override
    public String getType() {
        return configuration.getType();
    }

    @Override
    public boolean isCloseable() {
        return true;
    }

    @Override
    public String getTitle() {
        return configuration.getName();
    }

    @Override
    public GraphBox<ShaderFieldType> createPipelineGraphBox(Skin skin, String id, JSONObject data) {
        GraphBoxImpl<ShaderFieldType> result = new GraphBoxImpl<ShaderFieldType>(id, configuration, skin);
        addInputsAndOutputs(result, skin);
        AttributeUVBoxPart uvBoxPart = new AttributeUVBoxPart(skin);
        uvBoxPart.initialize(data);
        result.addGraphBoxPart(uvBoxPart);
        return result;
    }

    private void addInputsAndOutputs(GraphBoxImpl<ShaderFieldType> graphBox, Skin skin) {
        Iterator<GraphNodeInput<ShaderFieldType>> inputIterator = configuration.getNodeInputs().values().iterator();
        Iterator<GraphNodeOutput<ShaderFieldType>> outputIterator = configuration.getNodeOutputs().values().iterator();
        while (inputIterator.hasNext() || outputIterator.hasNext()) {
            GraphNodeInput<ShaderFieldType> input = null;
            GraphNodeOutput<ShaderFieldType> output = null;
            while (inputIterator.hasNext()) {
                input = inputIterator.next();
                if (input.isMainConnection()) {
                    graphBox.addTopConnector(input);
                    input = null;
                } else {
                    break;
                }
            }
            while (outputIterator.hasNext()) {
                output = outputIterator.next();
                if (output.isMainConnection()) {
                    graphBox.addBottomConnector(output);
                    output = null;
                } else {
                    break;
                }
            }

            if (input != null && output != null) {
                graphBox.addTwoSideGraphPart(skin, input, output);
            } else if (input != null) {
                graphBox.addInputGraphPart(skin, input);
            } else if (output != null) {
                graphBox.addOutputGraphPart(skin, output);
            }
        }
    }

    @Override
    public GraphBox<ShaderFieldType> createDefault(Skin skin, String id) {
        GraphBoxImpl<ShaderFieldType> result = new GraphBoxImpl<ShaderFieldType>(id, configuration, skin);
        addInputsAndOutputs(result, skin);
        result.addGraphBoxPart(new AttributeUVBoxPart(skin));
        return result;
    }

    private static class AttributeUVBoxPart extends Table implements GraphBoxPart<ShaderFieldType> {
        private final SelectBox<String> selectBox;

        public AttributeUVBoxPart(Skin skin) {
            selectBox = new SelectBox<String>(skin);
            selectBox.setItems("UV0", "UV1", "UV2", "UV3");
            add(new Label("Channel ", skin));
            add(selectBox).growX();
            row();
        }

        public void initialize(JSONObject data) {
            String coordinates = (String) data.get("channel");
            selectBox.setSelected(coordinates);
        }

        @Override
        public Actor getActor() {
            return this;
        }

        @Override
        public GraphBoxOutputConnector<ShaderFieldType> getOutputConnector() {
            return null;
        }

        @Override
        public GraphBoxInputConnector<ShaderFieldType> getInputConnector() {
            return null;
        }

        @Override
        public void serializePart(JSONObject object) {
            object.put("channel", selectBox.getSelected());
        }

        @Override
        public void dispose() {

        }
    }
}