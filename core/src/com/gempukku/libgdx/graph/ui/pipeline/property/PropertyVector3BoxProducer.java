package com.gempukku.libgdx.graph.ui.pipeline.property;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.gempukku.libgdx.graph.pipeline.PipelineFieldType;
import com.gempukku.libgdx.graph.ui.graph.GraphChangedEvent;
import com.gempukku.libgdx.graph.ui.graph.property.PropertyBox;
import com.gempukku.libgdx.graph.ui.graph.property.PropertyBoxImpl;
import com.gempukku.libgdx.graph.ui.graph.property.PropertyBoxProducer;
import com.gempukku.libgdx.graph.ui.graph.property.PropertyDefaultBox;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import org.json.simple.JSONObject;

public class PropertyVector3BoxProducer implements PropertyBoxProducer<PipelineFieldType> {
    @Override
    public PipelineFieldType getType() {
        return PipelineFieldType.Vector3;
    }

    @Override
    public PropertyBox<PipelineFieldType> createPropertyBox(Skin skin, String name, JSONObject jsonObject) {
        float x = ((Number) jsonObject.get("x")).floatValue();
        float y = ((Number) jsonObject.get("y")).floatValue();
        float z = ((Number) jsonObject.get("z")).floatValue();
        return createPropertyBoxDefault(skin, name, x, y, z);
    }

    @Override
    public PropertyBox<PipelineFieldType> createDefaultPropertyBox(Skin skin) {
        return createPropertyBoxDefault(skin, "New Vector3", 0f, 0f, 0f);
    }

    private PropertyBox<PipelineFieldType> createPropertyBoxDefault(Skin skin, String name, float v1, float v2, float v3) {
        final VisValidatableTextField v1Input = new VisValidatableTextField(Validators.FLOATS) {
            @Override
            public float getPrefWidth() {
                return 50;
            }
        };
        v1Input.setText(String.valueOf(v1));
        v1Input.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        v1Input.fire(new GraphChangedEvent(false, true));
                    }
                });
        final VisValidatableTextField v2Input = new VisValidatableTextField(Validators.FLOATS) {
            @Override
            public float getPrefWidth() {
                return 50;
            }
        };
        v2Input.setText(String.valueOf(v2));
        v2Input.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        v2Input.fire(new GraphChangedEvent(false, true));
                    }
                });
        final VisValidatableTextField v3Input = new VisValidatableTextField(Validators.FLOATS) {
            @Override
            public float getPrefWidth() {
                return 50;
            }
        };
        v3Input.setText(String.valueOf(v3));
        v3Input.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        v3Input.fire(new GraphChangedEvent(false, true));
                    }
                });

        final Table table = new Table();
        table.add(new Label("X ", skin));
        table.add(v1Input).grow();
        table.add(new Label("Y ", skin));
        table.add(v2Input).grow();
        table.add(new Label("Z ", skin));
        table.add(v3Input).grow();

        return new PropertyBoxImpl<PipelineFieldType>(skin,
                name,
                PipelineFieldType.Vector3,
                new PropertyDefaultBox() {
                    @Override
                    public Actor getActor() {
                        return table;
                    }

                    @Override
                    public JSONObject serializeData() {
                        JSONObject result = new JSONObject();
                        result.put("x", Float.parseFloat(v1Input.getText()));
                        result.put("y", Float.parseFloat(v2Input.getText()));
                        result.put("z", Float.parseFloat(v3Input.getText()));
                        return result;
                    }
                });
    }
}
