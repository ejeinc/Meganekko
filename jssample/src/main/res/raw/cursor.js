var cursor = scene.findObjectById('cursor');
if(cursor) {
    scene.on('update', function(frame) {
        var q = scene.getViewOrientation();
        var pos = new Vector3f(0, 0, -5);
        q.transform(pos);
        cursor.position(pos);
        cursor.rotation(q);
    });
}