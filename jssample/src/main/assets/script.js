var obj = scene.findObjectById('hello_world');

obj.on('swipeup', function() {
    obj.animate()
        .moveBy(new Vector3f(0, 1, 0))
        .start(app);
}).on('swipedown', function() {
    obj.animate()
        .moveBy(new Vector3f(0, -1, 0))
        .start(app);
}).on('swipeforward', function() {
    obj.animate()
        .rotateBy(0, Math.PI / 4, 0)
        .start(app);
}).on('swipeback', function() {
    obj.animate()
        .rotateBy(0, -Math.PI / 4, 0)
        .start(app);
});

scene.on('touchsingle', function() {
    app.setSceneFromRawResource(R.raw.scene);
});