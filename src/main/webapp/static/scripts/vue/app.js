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

new Vue({
    router,
    el: '#app',
    data: {
        csrf: null,
        oauth: null,
        oauth_exp: null,
        oauth_scopes: null,
        baseApi: `https://${window.location.hostname}:8443`
    },
    mounted: function () { //When created, will be executed
        this.csrf = this.getCsrfToken();
    },
    methods: {
        storeAuthInfos: function (body) {
            this.oauth = body["access_token"];
            this.oauth_exp = body["expires_in"];
            this.oauth_scopes = body["scope"].split(" ");
        },
        //---------------------------------------------------------------------------
        authSuccessEvent: function (body) {
            this.storeAuthInfos(body);
            router.push('game')
        },
        //---------------------------------------------------------------------------
        login: function () {
            let ref = this;
            let user = $('#username-input').val();
            let pwd = $('#password-input').val();

            if (!user || !pwd) {
                alertify.error("Password or the username cannot be empty!", 5);
                return;
            }

            $.ajax({
                url: `${this.baseApi}/oauth/token`,
                type: "POST",
                cache: false,
                timeout: 30000,
                datatype: "x-www-form-urlencoded",
                data: `username=${user}&password=${pwd}&grant_type=password`,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader("Authorization", "Basic Y2xpZW50SWQ6c2VjcmV0");
                    xhr.setRequestHeader("X-CSRF-TOKEN", ref.csrf);
                },
            }).done(this.authSuccessEvent).fail(function () {
                alert("login failed!");
            });
        },
        //---------------------------------------------------------------------------
        getCsrfToken: function () {
            return $('meta[name=_csrf]').attr("content");
        }
    }
});
