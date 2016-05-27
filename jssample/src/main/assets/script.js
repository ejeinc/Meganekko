// scene: Scene reference
// app : MeganekkoApp reference

// get SceneObject
var obj = scene.findObjectById('hello_world');

// Attach gesture events.
// Note that these events are only triggered while user is looking at this object.
obj.on('swipeup', function () {
    obj.animate()
        .moveBy(new Vector3f(0, 1, 0))
        .start(app);
}).on('swipedown', function () {
    obj.animate()
        .moveBy(new Vector3f(0, -1, 0))
        .start(app);
}).on('swipeforward', function () {
    obj.animate()
        .rotateBy(0, Math.PI / 4, 0)
        .start(app);
}).on('swipeback', function () {
    obj.animate()
        .rotateBy(0, -Math.PI / 4, 0)
        .start(app);
});

// scene events are always triggered if gesture is detected.
scene.on('touchsingle', function () {
    app.setSceneFromRawResource(R.raw.scene);
}).on('touchdouble', function () {
    app.context.createVrToastOnUiThread('Double tap detected!');
}).on('touchlongpress', function () {
    app.context.createVrToastOnUiThread('Long tap detected!');
});