/*
 *    Copyright 2014 - 2019 Yannick Watier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

const router = new VueRouter({
    mode: 'history',
    routes: [
        {path: '*', redirect: '/'},
        {path: '/', component: Login},
        {path: '/game', component: Game}
    ]
});

const vm = new Vue({
    router,
    el: '#app',
    data: {
        oauth: null,
        oauth_refresh: null,
        oauth_exp: null,
        oauth_scopes: null,
        baseApi: `${API_PROTOCOL}://${API_URL}:${API_PORT}`,
        baseApiWs: `${API_PROTOCOL_WS}://${API_URL_WS}:${API_PORT_WS}`
    },
    mounted: function () { //When created, will be executed
        this.csrf = this.getCsrfToken();
    },
    methods: {
        storeAuthInfos: function (body) {
            this.oauth = body["access_token"];
            this.oauth_refresh = body["refresh_token"];
            this.oauth_exp = body["expires_in"];
            this.oauth_scopes = body["scope"].split(" ");
        },
        //---------------------------------------------------------------------------
        authSuccessEvent: function (body) {
            this.storeAuthInfos(body);
            router.push('game')
        },
        //---------------------------------------------------------------------------
        getCsrfToken: function () {
            return $('meta[name=_csrf]').attr("content");
        }
    }
});
