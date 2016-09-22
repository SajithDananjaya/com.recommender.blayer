<%@ include file="header.jsp" %>
<link href="assets/css/main.css" rel="stylesheet" type="text/css"/>


<!-- MENU SECTION END-->
<div class="content-wrapper">
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <h4 class="page-head-line">Dashboard</h4>
            </div>
        </div>

        <div class="col-md-4 col-sm-4 col-xs-3"></div>

        <div class="col-md-4 col-sm-4 col-xs-6" style="position: relative">
            <a href="init.jsp" onclick="myFunction()">
                <div class="dashboard-div-wrapper bk-clr-three" id="deployDiv">
                    <i  class="fa fa-cogs dashboard-div-icon" ></i>
                    <div class="progress progress-striped active">
                        <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 40%">
                        </div>
                    </div>
                    <h5>Deploy Module </h5>
                </div>
            </a>

            <div id="loader-wrapper">
                <div id="loaderDiv"  style="display: none">
                    <div id="loader" style=" position: fixed; margin-top: 2%"></div>
                    <div class="loading" style="color: #cc0033;font-size: 20px; margin-top: 300px;"></div>
               <a href="index.jsp" class="btn btn-danger"><span class="glyphicon glyphicon-remove"></span> &nbsp;Cancel </a>
                </div>
            </div>         
        </div>
        <div class="col-md-4 col-sm-4 col-xs-3"></div>
    </div>



    <%@ include file="footer.jsp" %>
