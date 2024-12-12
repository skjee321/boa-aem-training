function add(a, b) {

        console.log('a=' + a);

        console.log('b=' + b);

        return a + b;

}

$( document ).ready(function() {
    console.log( "document loaded" );

    console.log('Sum = ', add(5, 5));
});


/*
function readyFn( jQuery ) {
    // Code to run when the document is ready.
}


(function($) {

    console.log('test');

    console.log(location.add(5, 5));

})(jQuery, location);*/

