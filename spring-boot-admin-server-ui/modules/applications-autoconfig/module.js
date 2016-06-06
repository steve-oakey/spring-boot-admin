/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

var angular = require('angular');

var module = angular.module('sba-applications-autoconfig', ['sba-applications']);
global.sbaModules.push(module.name);

module.controller('autoconfigCtrl', require('./controllers/autoconfigCtrl.js'));
module.component('sbaAutoconfigReport', require('./components/autoconfigReport.js'));

module.config(function ($stateProvider) {
  $stateProvider.state('applications.autoconfig', {
    url: '/autoconfig',
    templateUrl: 'applications-autoconfig/views/autoconfig.html',
    controller: 'autoconfigCtrl'
  });
});

module.run(function (ApplicationViews, $sce) {
  ApplicationViews.register({
    order: 90,
    title: $sce.trustAsHtml('<i class="fa fa-bug fa-fw"></i>Auto Config'),
    state: 'applications.autoconfig',
    show: function (application) {
      return application.managementUrl && application.statusInfo.status !== null && application.statusInfo.status !== 'OFFLINE';
    }
  });
});
