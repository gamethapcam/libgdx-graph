package com.gempukku.libgdx.graph.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gempukku.libgdx.graph.entity.GameEntity;
import com.gempukku.libgdx.graph.loader.GraphLoader;
import com.gempukku.libgdx.graph.pipeline.PipelineLoaderCallback;
import com.gempukku.libgdx.graph.pipeline.PipelineRenderer;
import com.gempukku.libgdx.graph.pipeline.RenderOutputs;
import com.gempukku.libgdx.graph.shader.sprite.GraphSprite;
import com.gempukku.libgdx.graph.shader.sprite.GraphSprites;
import com.gempukku.libgdx.graph.sprite.Sprite;
import com.gempukku.libgdx.graph.sprite.SpriteProducer;
import com.gempukku.libgdx.graph.sprite.StateBasedSprite;
import com.gempukku.libgdx.graph.sprite.def.PhysicsDef;
import com.gempukku.libgdx.graph.sprite.def.SpriteDef;
import com.gempukku.libgdx.graph.system.EntitySystem;
import com.gempukku.libgdx.graph.system.PhysicsSystem;
import com.gempukku.libgdx.graph.system.PlayerControlSystem;
import com.gempukku.libgdx.graph.system.TextureSystem;
import com.gempukku.libgdx.graph.system.camera.CameraController;
import com.gempukku.libgdx.graph.system.camera.FocusWindowCameraController;
import com.gempukku.libgdx.graph.system.camera.SpriteFocus;

import java.io.IOException;
import java.io.InputStream;

public class TestScene implements LibgdxGraphTestScene {
    private Array<Disposable> resources = new Array<>();
    private PipelineRenderer pipelineRenderer;
    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;

    private TextureSystem textureSystem;
    private CameraController cameraController;
    private PhysicsSystem physicsSystem;
    private EntitySystem entitySystem;
    private PlayerControlSystem playerControlSystem;

    private boolean debugRender = false;
    private Box2DDebugRenderer debugRenderer;
    private Matrix4 tmpMatrix;

    @Override
    public void initializeScene() {
        Box2D.init();
        WhitePixel.initialize();

        skin = createSkin();
        resources.add(skin);

        stage = createStage(skin);
        resources.add(stage);

        camera = createCamera();

        pipelineRenderer = loadPipelineRenderer();
        resources.add(pipelineRenderer);

        createSystems();

        Json json = new Json();

        loadEnvironment(json);

        GameEntity<? extends Sprite> playerEntity = readEntity(json, "sprite/playerBlueWizard.json");
        playerControlSystem.setPlayerEntity((GameEntity<StateBasedSprite>) playerEntity);

        cameraController = new FocusWindowCameraController(camera, new SpriteFocus(playerEntity.getSprite()),
                new Rectangle(0.1f, 0.1f, 0.4f, 0.4f),
                new Rectangle(0.2f, 0.1f, 0.2f, 0.4f), new Vector2(0.1f, 0.1f));
        resources.add(cameraController);

        Gdx.input.setInputProcessor(stage);

        if (debugRender) {
            debugRenderer = new Box2DDebugRenderer();
            tmpMatrix = new Matrix4();
        }
    }

    private void loadEnvironment(Json json) {
        readEntity(json, "sprite/ground.json");
    }

    private void createSystems() {
        textureSystem = new TextureSystem();
        resources.add(textureSystem);

        physicsSystem = new PhysicsSystem(-30f);
        resources.add(physicsSystem);

        entitySystem = new EntitySystem(pipelineRenderer);
        resources.add(entitySystem);

        playerControlSystem = new PlayerControlSystem();
        resources.add(playerControlSystem);
    }

    private GameEntity<? extends Sprite> readEntity(Json json, String path) {
        SpriteDef player = json.fromJson(SpriteDef.class, Gdx.files.internal(path));

        return createEntity(pipelineRenderer.getGraphSprites(), player);
    }

    private GameEntity<? extends Sprite> createEntity(GraphSprites graphSprites, SpriteDef spriteDef) {
        GraphSprite graphSprite = graphSprites.createSprite(spriteDef.getLayer(), spriteDef.getTags());
        GameEntity<? extends Sprite> gameEntity = entitySystem.createGameEntity(SpriteProducer.createSprite(textureSystem, graphSprite, spriteDef));
        PhysicsDef physicsDef = spriteDef.getPhysicsDef();
        String physicsType = physicsDef.getType();
        if (physicsType.equals("dynamic")) {
            gameEntity.createDynamicBody(physicsSystem, physicsDef.getColliderAnchor(), physicsDef.getColliderScale());
        } else if (physicsType.equals("static")) {
            gameEntity.createStaticBody(physicsSystem, physicsDef.getColliderAnchor(), physicsDef.getColliderScale());
        }
        return gameEntity;
    }

    private OrthographicCamera createCamera() {
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.position.set(0, 0, 0);
        camera.update();

        return camera;
    }

    private Skin createSkin() {
        return new Skin(Gdx.files.classpath("skin/default/uiskin.json"));
    }

    private Stage createStage(Skin skin) {
        Stage stage = new Stage(new ScreenViewport());

        Table tbl = new Table(skin);

        tbl.setFillParent(true);
        tbl.align(Align.topLeft);

        stage.addActor(tbl);
        return stage;
    }

    @Override
    public void resizeScene(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void renderScene() {
        float delta = Math.min(0.03f, Gdx.graphics.getDeltaTime());
        stage.act(delta);

        textureSystem.update(delta);
        playerControlSystem.update(delta);
        physicsSystem.update(delta);
        entitySystem.update(delta);
        cameraController.update(delta);

        pipelineRenderer.render(delta, RenderOutputs.drawToScreen);

        if (debugRender) {
            tmpMatrix.set(camera.combined).scale(PhysicsSystem.PIXELS_TO_METERS, PhysicsSystem.PIXELS_TO_METERS, 0);
            debugRenderer.render(physicsSystem.getWorld(), tmpMatrix);
        }
    }

    @Override
    public void disposeScene() {
        for (Disposable resource : resources) {
            resource.dispose();
        }

        WhitePixel.dispose();
    }

    private PipelineRenderer loadPipelineRenderer() {
        try {
            InputStream stream = Gdx.files.local("test.json").read();
            try {
                PipelineRenderer pipelineRenderer = GraphLoader.loadGraph(stream, new PipelineLoaderCallback());
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
        pipelineRenderer.setPipelineProperty("Camera", camera);
        pipelineRenderer.setPipelineProperty("Stage", stage);
    }
}