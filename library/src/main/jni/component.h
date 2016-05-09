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
 
#include "includes.h"

/***************************************************************************
 * Things which can be attached to a scene object.
 ***************************************************************************/

#ifndef COMPONENT_H_
#define COMPONENT_H_

#include "HybridObject.h"

namespace mgn {
class SceneObject;

class Component: public HybridObject {
public:
    Component() :
            HybridObject(), ownerObject() {
    }

    Component(SceneObject* owner_object) :
            ownerObject(owner_object) {
    }

    virtual ~Component() {
    }

    SceneObject* GetOwnerObject() const {
        return ownerObject;
    }

    void SetOwnerObject(SceneObject* ownerObject) {
        this->ownerObject = ownerObject;
    }

    void RemoveOwnerObject() {
        ownerObject = NULL;
    }

private:
    Component(const Component& component);
    Component(Component&& component);
    Component& operator=(const Component& component);
    Component& operator=(Component&& component);

private:
    SceneObject* ownerObject;
};

}
#endif
