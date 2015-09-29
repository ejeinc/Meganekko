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

/**
 * Represents a stock material shader.
 * 
 * You get these from {@link Material.ShaderType}; you can pass these to
 * {@link Material#GVRMaterial(GVRContext, MaterialShaderId)} and to
 * {@link Material#setShaderType(MaterialShaderId)}; you can <em>not</em>
 * pass these to
 * {@link MaterialShaderManager#getShaderMap(GVRCustomMaterialShaderId)}.
 */
public class StockMaterialShaderId extends MaterialShaderId {

    StockMaterialShaderId(int id) {
        super(id);
    }
}
