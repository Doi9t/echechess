<!--
~ Copyright 2014 - 2018 Yannick Watier
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
-->
<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1">

    <title>Login - EcheChess</title>
    <link rel="stylesheet" type="text/css" href="style/fontawesome-free/css/all.css">
    <link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
    <link rel="stylesheet" type="text/css" href="style/login.css">
    <link rel="stylesheet" type="text/css" href="style/game.css">
</head>

<body style="height: 100%">
<div id="app" style="height: 100%;width: 100%">
    <router-view></router-view>
</div>

<script src="scripts/lodash.min.js"></script>

<script src="scripts/jquery-3.3.1.min.js"></script>

<script src="style/fontawesome-free/attribution.js"></script>

<!--Need to be before the router-link-->
<script src="scripts/templates/alert.js"></script>
<script src="scripts/templates/login.js"></script>
<script src="scripts/templates/game.js"></script>

<script src="scripts/bootstrap.bundle.min.js"></script>

<script src="scripts/vue.min.js"></script>
<script src="scripts/vue-i18n.min.js"></script>
<script src="scripts/vue-router.min.js"></script>

<script src="scripts/templates/globalMixins.js"></script>

<script src="scripts/sockjs.min.js"></script>
<script src="scripts/stomp.min.js"></script>

<script src="scripts/apiOverrides.js"></script>

<script src="scripts/websocketDelegate.js"></script>

<script src="scripts/vue/app.js"></script>
</body>
</html>