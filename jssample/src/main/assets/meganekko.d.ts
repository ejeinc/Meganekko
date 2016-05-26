declare class MeganekkoApp {

  /*
   * Scene loading
   */
  setSceneFromRawResource(resId: number): void;
  setSceneFromAsset(assetPath: string): void;
  setSceneFromUri(uri: string): void;

  setScene(scene: Scene): void;
  getScene(): Scene;

  recenter(): void;

  /*
   * Threading
   */
  runOnGlThread(action: Function, delayMillis?: number): void;
  runOnUiThread(action: Function, delayMillis?: number): void;

  context: MeganekkoContext;
}

/**
 * For debug purpose only.
 */
declare class MeganekkoContext {
  createVrToastOnUiThread(text: string);
  showGazeCursor();
  hideGazeCursor();
}

declare class SceneObjectAnimator {
  moveTo(position: Vector3f): SceneObjectAnimator;
  moveBy(translation: Vector3f): SceneObjectAnimator;

  scaleTo(scale: Vector3f): SceneObjectAnimator;
  scaleBy(scale: Vector3f): SceneObjectAnimator;

  rotateTo(rotation: Quaternionf): SceneObjectAnimator;
  rotateTo(x: number, y: number, z: number): SceneObjectAnimator;

  rotateBy(rotate: Quaternionf): SceneObjectAnimator;
  rotateBy(x: number, y: number, z: number): SceneObjectAnimator;

  opacity(opacity: number): SceneObjectAnimator;
  interpolator(interpolator): SceneObjectAnimator;
  duration(duration: number): SceneObjectAnimator;
  delay(delay: number): SceneObjectAnimator;

  sequential(sequential: boolean): SceneObjectAnimator;
  onEnd(callback: Function): SceneObjectAnimator;
  start(app: MeganekkoApp);
}

declare class SceneObject {

  findObjectById(id: string): SceneObject;

  /*
   * Event handler
   */
  on(eventName: string, callback: (event) => void);
  off(eventName: string, callback?: (event) => void);

  /*
   * Transform
   */
  animate(): SceneObjectAnimator;

  position(vec: Vector3f): void;
  position(): Vector3f;

  scale(vec: Vector3f): void;
  scale(): Vector3f;

  rotation(rot: Quaternionf): void;
  rotation(): Quaternionf;

  /*
   * Visibility
   */
  setVisible(visible: boolean): void;
  isVisible(): boolean;
  isShown(): boolean;
  visible: boolean;

  setOpacity(opacity: number): void;
  getOpacity(): number;
  opacity: number;
}

declare class Scene extends SceneObject {
  getViewOrientation(): Quaternionf;

  setViewPosition(viewPosition: Vector3f): void;
  getViewPosition(): Vector3f;

  isLookingAt(target: SceneObject): boolean;
  getLookingPoint(target: SceneObject, axisInWorld: boolean): Vector3f;
}

declare class Vector3f {
  x: number;
  y: number;
  z: number;

  constructor();
  constructor(x: number, y: number, z: number);

  add(vec: Vector3f, dest?: Vector3f): Vector3f;
  add(x: number, y: number, z: number, dest?: Vector3f): Vector3f;

  mul(vec: Vector3f, dest?: Vector3f): Vector3f;
  mul(scalar: number, dest?: Vector3f): Vector3f;
  mul(x: number, y: number, z: number, dest?: Vector3f): Vector3f;

  div(vec: Vector3f, dest?: Vector3f): Vector3f;
  div(scalar: number, dest?: Vector3f): Vector3f;
  div(x: number, y: number, z: number, dest?: Vector3f): Vector3f;

  rotate(quat: Quaternionf, dest?: Vector3f): Vector3f;

  lengthSquared(): number;
  length(): number;

  normalize(dest?: Vector3f): Vector3f;

  cross(v: Vector3f, dest?: Vector3f): Vector3f;
  cross(x: number, y: number, z: number, dest?: Vector3f): Vector3f;

  distance(v: Vector3f): number;
  distance(x: number, y: number, z: number): number;

  distanceSquared(v: Vector3f): number;
  distanceSquared(x: number, y: number, z: number): number;

  dot(v: Vector3f): number;
  dot(x: number, y: number, z: number): number;

  min(v: Vector3f): Vector3f;
  max(v: Vector3f): Vector3f;

  zero(): Vector3f;
  negate(dest?: Vector3f): Vector3f;
}

declare class Quaternionf {
  x: number;
  y: number;
  z: number;
  w: number;

  constructor();
  constructor(x: number, y: number, z: number, w: number);
  transform(vec: Vector3f);

  normalize(dest?: Quaternionf);
  add(q: Quaternionf, dest?: Quaternionf);
  dot(q: Quaternionf);
  angle(): number;

  set(x: number, y: number, z: number, w?: number): Quaternionf;
  set(q: Quaternionf): Quaternionf;
  setAngleAxis(angle: number, x: number, y: number, z: number): Quaternionf;

  rotationAxis(angle: number, axisX: number, axisY: number, axisZ: number): Quaternionf;
  rotationAxis(angle: number, axis: Vector3f): Quaternionf;
}

declare class Frame {
  getPredictedDisplayTimeInSeconds(): number;
  getDeltaSeconds(): number;
  getFrameNumber(): number;
  getSwipeFraction(): number;
  getButtonState(): number;
  getButtonPressed(): number;
  getButtonReleased(): number;
}

declare class JoyButton {
  BUTTON_A: number;
  BUTTON_B: number;
  BUTTON_X: number;
  BUTTON_Y: number;
  BUTTON_START: number;
  BUTTON_BACK: number;
  BUTTON_SELECT: number;
  BUTTON_MENU: number;
  BUTTON_RIGHT_TRIGGER: number;
  BUTTON_LEFT_TRIGGER: number;
  BUTTON_DPAD_UP: number;
  BUTTON_DPAD_DOWN: number;
  BUTTON_DPAD_LEFT: number;
  BUTTON_DPAD_RIGHT: number;
  BUTTON_LSTICK_UP: number;
  BUTTON_LSTICK_DOWN: number;
  BUTTON_LSTICK_LEFT: number;
  BUTTON_LSTICK_RIGHT: number;
  BUTTON_RSTICK_UP: number;
  BUTTON_RSTICK_DOWN: number;
  BUTTON_RSTICK_LEFT: number;
  BUTTON_RSTICK_RIGHT: number;
  BUTTON_TOUCH: number;
  BUTTON_SWIPE_UP: number;
  BUTTON_SWIPE_DOWN: number;
  BUTTON_SWIPE_FORWARD: number;
  BUTTON_SWIPE_BACK: number;
  BUTTON_TOUCH_WAS_SWIPE: number;
  BUTTON_TOUCH_SINGLE: number;
  BUTTON_TOUCH_DOUBLE: number;
  BUTTON_TOUCH_LONGPRESS: number;

  static contains(buttonState: number, code: number): boolean;
}

declare class KeyCode {
  OVR_KEY_NONE: number;

  OVR_KEY_LCONTROL: number;
  OVR_KEY_RCONTROL: number;
  OVR_KEY_LSHIFT: number;
  OVR_KEY_RSHIFT: number;
  OVR_KEY_LALT: number;
  OVR_KEY_RALT: number;
  OVR_KEY_MENU: number;

  OVR_KEY_UP: number;
  OVR_KEY_DOWN: number;
  OVR_KEY_LEFT: number;
  OVR_KEY_RIGHT: number;

  OVR_KEY_F1: number;
  OVR_KEY_F2: number;
  OVR_KEY_F3: number;
  OVR_KEY_F4: number;
  OVR_KEY_F5: number;
  OVR_KEY_F6: number;
  OVR_KEY_F7: number;
  OVR_KEY_F8: number;
  OVR_KEY_F9: number;
  OVR_KEY_F10: number;
  OVR_KEY_F11: number;
  OVR_KEY_F12: number;

  OVR_KEY_RETURN: number;
  OVR_KEY_SPACE: number;
  OVR_KEY_INSERT: number;
  OVR_KEY_DELETE: number;
  OVR_KEY_HOME: number;
  OVR_KEY_END: number;
  OVR_KEY_PAGEUP: number;
  OVR_KEY_PAGEDOWN: number;
  OVR_KEY_SCROLL_LOCK: number;
  OVR_KEY_PAUSE: number;
  OVR_KEY_PRINT_SCREEN: number;
  OVR_KEY_NUM_LOCK: number;
  OVR_KEY_CAPSLOCK: number;
  OVR_KEY_ESCAPE: number;
  OVR_KEY_BACK: number;
  OVR_KEY_SYS_REQ: number;
  OVR_KEY_BREAK: number;

  OVR_KEY_KP_DIVIDE: number;
  OVR_KEY_KP_MULTIPLY: number;
  OVR_KEY_KP_ADD: number;
  OVR_KEY_KP_SUBTRACT: number;
  OVR_KEY_KP_ENTER: number;
  OVR_KEY_KP_DECIMAL: number;
  OVR_KEY_KP_0: number;
  OVR_KEY_KP_1: number;
  OVR_KEY_KP_2: number;
  OVR_KEY_KP_3: number;
  OVR_KEY_KP_4: number;
  OVR_KEY_KP_5: number;
  OVR_KEY_KP_6: number;
  OVR_KEY_KP_7: number;
  OVR_KEY_KP_8: number;
  OVR_KEY_KP_9: number;

  OVR_KEY_TAB: number;
  OVR_KEY_COMMA: number;
  OVR_KEY_PERIOD: number;
  OVR_KEY_LESS: number;
  OVR_KEY_GREATER: number;
  OVR_KEY_FORWARD_SLASH: number;
  OVR_KEY_BACK_SLASH: number;
  OVR_KEY_QUESTION_MARK: number;
  OVR_KEY_SEMICOLON: number;
  OVR_KEY_COLON: number;
  OVR_KEY_APOSTROPHE: number;
  OVR_KEY_QUOTE: number;
  OVR_KEY_OPEN_BRACKET: number;
  OVR_KEY_CLOSE_BRACKET: number;
  OVR_KEY_CLOSE_BRACE: number;
  OVR_KEY_OPEN_BRACE: number;
  OVR_KEY_BAR: number;
  OVR_KEY_TILDE: number;
  OVR_KEY_GRAVE: number;

  OVR_KEY_1: number;
  OVR_KEY_2: number;
  OVR_KEY_3: number;
  OVR_KEY_4: number;
  OVR_KEY_5: number;
  OVR_KEY_6: number;
  OVR_KEY_7: number;
  OVR_KEY_8: number;
  OVR_KEY_9: number;
  OVR_KEY_0: number;
  OVR_KEY_EXCLAMATION: number;
  OVR_KEY_AT: number;
  OVR_KEY_POUND: number;
  OVR_KEY_DOLLAR: number;
  OVR_KEY_PERCENT: number;
  OVR_KEY_CARET: number;
  OVR_KEY_AMPERSAND: number;
  OVR_KEY_ASTERISK: number;
  OVR_KEY_OPEN_PAREN: number;
  OVR_KEY_CLOSE_PAREN: number;
  OVR_KEY_MINUS: number;
  OVR_KEY_UNDERSCORE: number;
  OVR_KEY_PLUS: number;
  OVR_KEY_EQUALS: number;
  OVR_KEY_BACKSPACE: number;

  OVR_KEY_A: number;
  OVR_KEY_B: number;
  OVR_KEY_C: number;
  OVR_KEY_D: number;
  OVR_KEY_E: number;
  OVR_KEY_F: number;
  OVR_KEY_G: number;
  OVR_KEY_H: number;
  OVR_KEY_I: number;
  OVR_KEY_J: number;
  OVR_KEY_K: number;
  OVR_KEY_L: number;
  OVR_KEY_M: number;
  OVR_KEY_N: number;
  OVR_KEY_O: number;
  OVR_KEY_P: number;
  OVR_KEY_Q: number;
  OVR_KEY_R: number;
  OVR_KEY_S: number;
  OVR_KEY_T: number;
  OVR_KEY_U: number;
  OVR_KEY_V: number;
  OVR_KEY_W: number;
  OVR_KEY_X: number;
  OVR_KEY_Y: number;
  OVR_KEY_Z: number;

  OVR_KEY_VOLUME_MUTE: number;
  OVR_KEY_VOLUME_UP: number;
  OVR_KEY_VOLUME_DOWN: number;
  OVR_KEY_MEDIA_NEXT_TRACK: number;
  OVR_KEY_MEDIA_PREV_TRACK: number;
  OVR_KEY_MEDIA_STOP: number;
  OVR_KEY_MEDIA_PLAY_PAUSE: number;
  OVR_KEY_LAUNCH_APP1: number;
  OVR_KEY_LAUNCH_APP2: number;

  OVR_KEY_BUTTON_A: number;
  OVR_KEY_BUTTON_B: number;
  OVR_KEY_BUTTON_C: number;
  OVR_KEY_BUTTON_X: number;
  OVR_KEY_BUTTON_Y: number;
  OVR_KEY_BUTTON_Z: number;
  OVR_KEY_BUTTON_START: number;
  OVR_KEY_BUTTON_SELECT: number;
  OVR_KEY_BUTTON_MENU: number;
  OVR_KEY_LEFT_TRIGGER: number;
  OVR_KEY_BUTTON_L1: number;
  OVR_KEY_RIGHT_TRIGGER: number;
  OVR_KEY_BUTTON_R1: number;
  OVR_KEY_DPAD_UP: number;
  OVR_KEY_DPAD_DOWN: number;
  OVR_KEY_DPAD_LEFT: number;
  OVR_KEY_DPAD_RIGHT: number;
  OVR_KEY_LSTICK_UP: number;
  OVR_KEY_LSTICK_DOWN: number;
  OVR_KEY_LSTICK_LEFT: number;
  OVR_KEY_LSTICK_RIGHT: number;
  OVR_KEY_RSTICK_UP: number;
  OVR_KEY_RSTICK_DOWN: number;
  OVR_KEY_RSTICK_LEFT: number;
  OVR_KEY_RSTICK_RIGHT: number;

  OVR_KEY_BUTTON_LEFT_SHOULDER: number;
  OVR_KEY_BUTTON_L2: number;
  OVR_KEY_BUTTON_RIGHT_SHOULDER: number;
  OVR_KEY_BUTTON_R2: number;
  OVR_KEY_BUTTON_LEFT_THUMB: number;
  OVR_KEY_BUTTON_THUMBL: number;
  OVR_KEY_BUTTON_RIGHT_THUMB: number;
  OVR_KEY_BUTTON_THUMBR: number;

  OVR_KEY_MAX: number;
}

/*
 * Global variables
 */
declare const scene: Scene;
declare const app: MeganekkoApp;
declare const R: any;