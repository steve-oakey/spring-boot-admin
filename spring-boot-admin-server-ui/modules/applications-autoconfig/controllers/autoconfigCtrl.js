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

module.exports = function ($scope, $log, application) {
  'ngInject';

  $scope.positiveMatches = null;
  $scope.negativeMatches = null;

  $scope.refresh = function() {
    $scope.positiveMatches = [];
    $scope.negativeMatches = [];
    application.getAutoConfigReport().then(function (response) {
      angular.forEach(response.data.positiveMatches, parseReport, $scope.positiveMatches);
      angular.forEach(response.data.negativeMatches, parseReport, $scope.negativeMatches);
    });
  };
  
  function parseReport(conditions, bean) {
    this.push({
      bean: bean,
      conditions: conditions
    });
  }
  
  $scope.refresh();
};
