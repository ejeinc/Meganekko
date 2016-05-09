package com.eje_c.meganekko.scene_objects;

import com.eje_c.meganekko.Mesh;
import com.eje_c.meganekko.SceneObject;

public class GlobeSceneObject extends SceneObject {
    public GlobeSceneObject() {
        Mesh mesh = new Mesh();
        mesh.buildGlobe(1, 1);
        mesh(mesh);
    }
}
