package org.meganekkovr.sampleobj;

import android.content.Context;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.meganekkovr.Entity;
import org.meganekkovr.GeometryComponent;
import org.meganekkovr.xml.XmlAttributeParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * This enables XML attribute {@code obj="@raw/xxx"}
 */
public class ObjHandler implements XmlAttributeParser.XmlAttributeHandler {

    @Override
    public String attributeName() {
        return "obj";
    }

    @Override
    public void parse(Entity entity, String rawValue, Context context) {

        if (XmlAttributeParser.isRawResource(rawValue)) {

            // rawValue = @raw/xxx

            int resId = XmlAttributeParser.toResourceId(rawValue, context);
            if (resId == 0) return;

            try (InputStream stream = context.getResources().openRawResource(resId)) {
                GeometryComponent geo = new GeometryComponent();
                loadObj(geo, stream);
                entity.add(geo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadObj(GeometryComponent geo, InputStream stream) throws IOException {

        Obj obj = Obj.parse(stream);

        float[] positions = new float[obj.v.size() * 3];
        {
            int i = 0;
            for (Vector3f v : obj.v) {
                positions[i++] = v.x;
                positions[i++] = v.y;
                positions[i++] = v.z;
            }
        }

        float[] colors = new float[obj.v.size() * 4];
        Arrays.fill(colors, 1);

        int[] triangles = new int[obj.f.size() * 3];
        float[] uvs = new float[obj.vt.size() * 2];
        {
            int i = 0;
            for (Obj.Face face : obj.f) {
                for (Obj.Face.Indexes indexes : face.indexes) {
                    int vIdx = indexes.vertex - 1;
                    triangles[i++] = vIdx;

                    int uvIdx = indexes.texture - 1;
                    Vector2f uv = obj.vt.get(uvIdx);
                    uvs[vIdx * 2] = uv.x;
                    uvs[vIdx * 2 + 1] = 1 - uv.y;
                }
            }
        }

        geo.build(positions, colors, uvs, triangles);
    }
}
