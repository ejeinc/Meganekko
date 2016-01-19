/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko;

import android.util.SparseArray;

/**
 * Opaque type that specifies a material shader.
 * <p/>
 * The inheritance tree represents the fact that stock shaders do not use
 * {@link MaterialMap name maps.}
 */
public abstract class MaterialShaderId {
    private final static SparseArray<MaterialShaderId> sIds = new SparseArray<>();

    final int ID;

    protected MaterialShaderId(int id) {
        ID = id;
        put(id, this);
    }

    /**
     * @deprecated Probably unnecessary ...
     */
    @SuppressWarnings("unchecked")
    protected final static <T extends MaterialShaderId> T get(int id) {
        return (T) sIds.get(id);
    }

    protected final static void put(int id, MaterialShaderId wrapper) {
        sIds.put(id, wrapper);
    }
}