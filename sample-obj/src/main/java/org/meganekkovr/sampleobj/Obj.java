package org.meganekkovr.sampleobj;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple .obj file parser.
 */
public class Obj {

    public final List<Vector3f> v;
    public final List<Vector2f> vt;
    public final List<Vector3f> vn;
    public final List<Face> f;

    public Obj(List<Vector3f> v, List<Vector2f> vt, List<Vector3f> vn, List<Face> f) {
        this.v = v;
        this.vt = vt;
        this.vn = vn;
        this.f = f;
    }

    public static class Face {
        public final List<Indexes> indexes;

        public Face(List<Indexes> indexes) {
            this.indexes = indexes;
        }

        public static class Indexes {
            public final int vertex;
            public final int texture;
            public final int normal;

            public Indexes(int vertex, int texture, int normal) {
                this.vertex = vertex;
                this.texture = texture;
                this.normal = normal;
            }
        }
    }

    /**
     * Parse .obj data from stream.
     *
     * @param stream stream
     * @return new Obj instance
     * @throws IOException
     */
    public static Obj parse(InputStream stream) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            String line;
            List<Vector3f> v = new ArrayList<>();
            List<Vector2f> vt = new ArrayList<>();
            List<Vector3f> vn = new ArrayList<>();
            List<Face> f = new ArrayList<>();

            while (true) {

                line = reader.readLine();
                if (line == null) break;

                if (line.startsWith("v ")) {

                    // ex. v 1.000000 -1.000000 -1.000000
                    String[] strs = line.split("\\s+");
                    v.add(new Vector3f(Float.parseFloat(strs[1]), Float.parseFloat(strs[2]), Float.parseFloat(strs[3])));

                } else if (line.startsWith("vt ")) {

                    // ex. vt 0.748573 0.750412
                    String[] strs = line.split("\\s+");
                    vt.add(new Vector2f(Float.parseFloat(strs[1]), Float.parseFloat(strs[2])));

                } else if (line.startsWith("vn ")) {

                    // ex. vn -0.000001 0.000000 1.000000
                    String[] strs = line.split("\\s+");
                    vn.add(new Vector3f(Float.parseFloat(strs[1]), Float.parseFloat(strs[2]), Float.parseFloat(strs[3])));

                } else if (line.startsWith("f ")) {

                    // ex. f 5/1/1 1/2/1 4/3/1
                    String[] strs = line.split("\\s+");

                    List<Face.Indexes> indexes = new ArrayList<>();

                    for (int i = 1; i < strs.length; i++) {
                        String[] parts = strs[i].split("/");
                        Face.Indexes idx = new Face.Indexes(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts.length == 3 ? Integer.parseInt(parts[2]) : 0);
                        indexes.add(idx);
                    }

                    f.add(new Face(indexes));
                }
            }

            return new Obj(v, vt, vn, f);
        }
    }
}
