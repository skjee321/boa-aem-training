var location = (function ($) {

     function init() {

        // some code...
     }

     function add(a, b) {

        console.log('a=' + a);

        console.log('b=' + b);

        return a + b;

     }

     return {

         add:add

     };

})(jQuery);

location.init();
