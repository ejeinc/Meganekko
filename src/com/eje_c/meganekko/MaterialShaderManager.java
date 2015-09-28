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

import java.util.HashMap;
import java.util.Map;

/**
 * Manages custom shaders, for rendering scene objects.
 * 
 * Get the singleton from {@link VrContext#getMaterialShaderManager()}.
 */
public class MaterialShaderManager extends
        BaseShaderManager<MaterialMap, CustomMaterialShaderId>
        implements ShaderManagers<MaterialMap, CustomMaterialShaderId> {

    private final Map<CustomMaterialShaderId, MaterialMap> materialMaps = new HashMap<CustomMaterialShaderId, MaterialMap>();

    MaterialShaderManager(VrContext vrContext) {
        super(vrContext, NativeShaderManager.ctor());
    }

    @Override
    public CustomMaterialShaderId addShader(String vertexShader,
            String fragmentShader) {
        final int shaderId = NativeShaderManager.addCustomShader(getNative(),
                vertexShader, fragmentShader);
        CustomMaterialShaderId result = new CustomMaterialShaderId(
                shaderId);
        materialMaps.put(result, retrieveShaderMap(result));
        return result;
    }

    @Override
    public MaterialMap getShaderMap(CustomMaterialShaderId id) {
        return materialMaps.get(id);
    }

    @SuppressWarnings("resource")
    private MaterialMap retrieveShaderMap(CustomMaterialShaderId id) {
        long ptr = NativeShaderManager.getCustomShader(getNative(), id.ID);
        return ptr == 0 ? null : new MaterialMap(getVrContext(), ptr);
    }
}

class NativeShaderManager {
    static native long ctor();

    static native int addCustomShader(long shaderManager, String vertexShader,
            String fragmentShader);

    static native long getCustomShader(long shaderManager, int id);
}
