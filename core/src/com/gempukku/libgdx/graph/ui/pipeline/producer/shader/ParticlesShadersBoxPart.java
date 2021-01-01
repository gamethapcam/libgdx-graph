package com.gempukku.libgdx.graph.ui.pipeline.producer.shader;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.JsonValue;
import com.gempukku.libgdx.graph.pipeline.PipelineFieldType;
import com.gempukku.libgdx.graph.shader.ShaderFieldType;
import com.gempukku.libgdx.graph.ui.UIGraphConfiguration;
import com.gempukku.libgdx.graph.ui.graph.GetSerializedGraph;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxInputConnector;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxOutputConnector;
import com.gempukku.libgdx.graph.ui.graph.GraphBoxPart;
import com.gempukku.libgdx.graph.ui.graph.GraphDesignTab;
import com.gempukku.libgdx.graph.ui.graph.GraphRemoved;
import com.gempukku.libgdx.graph.ui.graph.RequestGraphOpen;
import com.gempukku.libgdx.graph.ui.pipeline.producer.shader.registry.GraphShaderTemplate;
import com.gempukku.libgdx.graph.ui.pipeline.producer.shader.registry.GraphShaderTemplateRegistry;
import com.gempukku.libgdx.graph.ui.shader.common.UICommonShaderConfiguration;
import com.gempukku.libgdx.graph.ui.shader.particles.UIParticlesShaderConfiguration;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogListener;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisScrollPane;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ParticlesShadersBoxPart extends Table implements GraphBoxPart<PipelineFieldType> {
    private static UIGraphConfiguration<ShaderFieldType>[] graphConfigurations = new UIGraphConfiguration[]{
            new UIParticlesShaderConfiguration(),
            new UICommonShaderConfiguration()
    };

    private static final int EDIT_WIDTH = 50;
    private static final int REMOVE_WIDTH = 80;
    private final VerticalGroup shaderGroup;
    private final Skin skin;
    private List<ShaderInfo> shaders = new LinkedList<>();

    public ParticlesShadersBoxPart(Skin skin) {
        this.skin = skin;

        shaderGroup = new VerticalGroup();
        shaderGroup.top();
        shaderGroup.grow();

        Table table = new Table(skin);
        table.add("Tag").colspan(3).growX();
        table.row();

        VisScrollPane scrollPane = new VisScrollPane(shaderGroup);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);

        table.add(scrollPane).grow().row();

        final TextButton newShader = new TextButton("New Effect", skin);
        newShader.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        PopupMenu popupMenu = new PopupMenu();
                        for (final GraphShaderTemplate graphShaderTemplate : GraphShaderTemplateRegistry.particlesShaderTemplateList) {
                            if (graphShaderTemplate != null) {
                                MenuItem menuItem = new MenuItem(graphShaderTemplate.getTitle());
                                popupMenu.addItem(menuItem);
                                menuItem.addListener(
                                        new ChangeListener() {
                                            @Override
                                            public void changed(ChangeEvent event, Actor actor) {
                                                graphShaderTemplate.invokeTemplate(getStage(),
                                                        new GraphShaderTemplate.Callback() {
                                                            @Override
                                                            public void addShader(String tag, JsonValue shader) {
                                                                String id = UUID.randomUUID().toString().replace("-", "");
                                                                addShaderGraph(id, tag, shader);
                                                            }
                                                        });
                                            }
                                        });
                            } else {
                                popupMenu.addSeparator();
                            }
                        }
                        popupMenu.showMenu(getStage(), newShader);
                    }
                });

        Table buttons = new Table(skin);
        buttons.add(newShader);
        table.add(buttons).growX().row();

        add(table).grow().row();
    }

    @Override
    public float getPrefWidth() {
        return 300;
    }

    @Override
    public float getPrefHeight() {
        return 200;
    }

    @Override
    public Actor getActor() {
        return this;
    }

    @Override
    public GraphBoxOutputConnector<PipelineFieldType> getOutputConnector() {
        return null;
    }

    @Override
    public GraphBoxInputConnector<PipelineFieldType> getInputConnector() {
        return null;
    }

    @Override
    public void dispose() {

    }

    private void addShaderGraph(String id, String tag, JsonValue shader) {
        ShaderInfo shaderInfo = new ShaderInfo(id, tag, shader);
        shaders.add(shaderInfo);
        shaderGroup.addActor(shaderInfo.getActor());
    }

    private void removeShaderGraph(ShaderInfo shaderInfo) {
        shaders.remove(shaderInfo);
        shaderGroup.removeActor(shaderInfo.getActor());
    }

    public void initialize(JsonValue data) {
        if (data != null) {
            JsonValue shaderArray = data.get("shaders");
            for (JsonValue shaderObject : shaderArray) {
                String id = shaderObject.getString("id");
                String tag = shaderObject.getString("tag");
                JsonValue shader = shaderObject.get("shader");
                addShaderGraph(id, tag, shader);
            }
        }
    }

    @Override
    public void serializePart(JsonValue object) {
        JsonValue shaderArray = new JsonValue(JsonValue.ValueType.array);
        for (ShaderInfo shader : shaders) {
            JsonValue shaderObject = new JsonValue(JsonValue.ValueType.object);
            shaderObject.addChild("id", new JsonValue(shader.getId()));
            shaderObject.addChild("tag", new JsonValue(shader.getTag()));
            GetSerializedGraph event = new GetSerializedGraph(shader.getId());
            fire(event);
            JsonValue shaderGraph = event.getGraph();
            if (shaderGraph == null)
                shaderGraph = shader.getInitialShaderJson();
            shaderObject.addChild("shader", shaderGraph);
            shaderArray.addChild(shaderObject);
        }

        object.addChild("shaders", shaderArray);
    }

    private class ShaderInfo {
        private String id;
        private JsonValue initialShaderJson;
        private Table table;
        private TextField textField;

        public ShaderInfo(final String id, String tag, final JsonValue initialShaderJson) {
            this.id = id;
            this.initialShaderJson = initialShaderJson;
            table = new Table(skin);
            textField = new TextField(tag, skin);
            textField.setMessageText("Effect Tag");
            table.add(textField).growX();
            final TextButton editButton = new TextButton("Edit", skin);
            editButton.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            editButton.fire(new RequestGraphOpen(id, "Shader - " + textField.getText(), initialShaderJson,
                                    GraphDesignTab.Type.Particle_Effect, graphConfigurations));
                        }
                    });
            table.add(editButton).width(EDIT_WIDTH);
            final TextButton deleteButton = new TextButton("Remove", skin);
            deleteButton.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            Dialogs.showOptionDialog(getStage(), "Confirm", "Would you like to remove the effect?",
                                    Dialogs.OptionDialogType.YES_CANCEL, new OptionDialogListener() {
                                        @Override
                                        public void yes() {
                                            fire(new GraphRemoved(id));
                                            removeShaderGraph(ParticlesShadersBoxPart.ShaderInfo.this);
                                        }

                                        @Override
                                        public void no() {

                                        }

                                        @Override
                                        public void cancel() {

                                        }
                                    });
                        }
                    });
            table.add(deleteButton).width(REMOVE_WIDTH);
            table.row();
        }

        public String getId() {
            return id;
        }

        public Table getActor() {
            return table;
        }

        public String getTag() {
            return textField.getText();
        }

        public JsonValue getInitialShaderJson() {
            return initialShaderJson;
        }
    }
}
