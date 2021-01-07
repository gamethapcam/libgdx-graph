package com.gempukku.libgdx.graph.system;

import com.badlogic.ashley.core.EntitySystem;
import com.gempukku.libgdx.graph.system.camera.constraint.ConstraintCameraFocusController;

public class CameraSystem extends EntitySystem {
    private ConstraintCameraFocusController constraintCameraController;

    public CameraSystem(int priority) {
        super(priority);
    }

    public void setConstraintCameraController(ConstraintCameraFocusController constraintCameraController) {
        this.constraintCameraController = constraintCameraController;
    }

    @Override
    public void update(float deltaTime) {
        constraintCameraController.update(deltaTime);
    }
}
