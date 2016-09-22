    function myFunction() {
        document.getElementById('deployDiv').style.display = 'none';
        document.getElementById('loaderDiv').style.display = 'block';
    }


    i = 0;
    setInterval(function () {
        i = ++i % 6;
        $(".loading").text("Initiating " + Array(i + 1).join("."));
    }, 800);





