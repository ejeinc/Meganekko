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
 * A RenderPass let one render the same scene object multiple times with different settings. This is useful to
 * achieve effects like outline in cartoon-ish rendering or computing addictive lights for instance.
 * 
 * The benefit of using a render pass over duplicating the object and rendering twice is that like culling, transform and
 * skinning are performed only once.
 * 
 * A render pass encapsulates a material and all rendering states that can be set per pass. 
 * 
 *
 */
public class RenderPass extends HybridObject {
    
    private Material mMaterial;
    private GVRCullFaceEnum mCullFace;
    
    public enum GVRCullFaceEnum {
        /**
         * Tell Graphics API to discard back faces. This value is assumed by
         * default.
         */
        Back(0),

        /**
         * Tell Graphics API to discard front faces.
         */
        Front(1),

        /**
         * Tell Graphics API render both front and back faces.
         */
        None(2);
        
        private final int mValue;
        
        private GVRCullFaceEnum(int value) {
            mValue = value;
        }
        
        public static GVRCullFaceEnum fromInt(int value) {
            switch (value) {
            case 1:
                return GVRCullFaceEnum.Front;
                
            case 2:
                return GVRCullFaceEnum.None;
                
            default:
                return GVRCullFaceEnum.Back;
            }
        }
        public int getValue() {
            return mValue;
        }
    }
    
    /**
     * Constructor.
     * 
     * @param gvrContext
     *            Current {@link VrContext}
     */
    public RenderPass(VrContext gvrContext) {
        super(gvrContext, NativeRenderPass.ctor());
        mMaterial = new Material(gvrContext);
        mCullFace = GVRCullFaceEnum.Back;
    }

    /**
     * Set the {@link Material material} for this pass.
     * 
     * @param material
     *            The {@link Material material} this {@link RenderPass pass}
     *            will be rendered with.
     */
    public void setMaterial(Material material) {
        mMaterial = material;
        NativeRenderPass.setMaterial(getNative(), material.getNative());
    }
    
    /**
     * @return The {@link Material material} this {@link RenderPass pass} will
     *         being rendered with.
     */
    public Material getMaterial() {
        return mMaterial;
    }
    
    /**
     * Set the {@link GVRCullFaceEnum face} to be culled when rendering this {@link RenderPass pass}
     * 
     * @param cullFace
     *            {@code GVRCullFaceEnum.Back} Tells Graphics API to discard
     *            back faces, {@code GVRCullFaceEnum.Front} Tells Graphics API
     *            to discard front faces, {@code GVRCullFaceEnum.None} Tells
     *            Graphics API to not discard any face
     */
    public void setCullFace(GVRCullFaceEnum cullFace) {
        mCullFace = cullFace;
        NativeRenderPass.setCullFace(getNative(), cullFace.getValue());
    }
    
    /**
     * @return The current {@link GVRCullFaceEnum face} to be culled.
     */
    public GVRCullFaceEnum getCullFace() {
        return mCullFace;
    }
}

class NativeRenderPass {
    
    static native long ctor();
    
    static native void setMaterial(long renderPass, long material);
    
    static native void setCullFace(long renderPass, int cullFace);
}
