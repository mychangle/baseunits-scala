/*
 * Copyright 2011 Tricreo Inc and the Others.
 * lastModified : 2011/04/22
 *
 * This file is part of Tricreo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.tricreo.baseunits.scala.timeutil

import java.util.TimeZone
import jp.tricreo.baseunits.scala.time.{CalendarDate, TimeSource}

/**時計を表すクラス。
 * このクラスはステートを持つstaticなユーティリティクラスである。
 *
 * @author j5ik2o
 */
object Clock {

  private[this] var _timeSource: TimeSource = _

  /**日付の算出に使用する [[java.util.TimeZone]] */
  private[this] var _defaultTimeZone: TimeZone = _

  /**この時計が日付の算出に使用する [[java.util.TimeZone]] を取得する。
   *
   * @return 日付の算出に使用する [[java.util.TimeZone]]
   */
  def defaultTimeZone: TimeZone = {
    // There is no reasonable automatic default.
    _defaultTimeZone
  }

  def defaultTimeZone_=(value: TimeZone) {
    _defaultTimeZone = value
  }

  /**現在時刻を取得する。
   *
   * @return 現在時刻
   */
  def now = timeSource.now

  /**このクラスが保持するステートをリセットする。
   *
   * このクラスは、staticに [[java.util.TimeZone]]
   * と[[jp.tricreo.baseunits.scala.time.TimeSource]] を保持している。
   */
  def reset {
    _defaultTimeZone = null
    _timeSource = null
  }

  /**[[jp.tricreo.baseunits.scala.timeutil.SystemClock]]を取得する。
   *
   * デフォルトでは [[jp.tricreo.baseunits.scala.timeutil.SystemClock]] を使用する。
   *
   * @return [[jp.tricreo.baseunits.scala.time.TimeSource]]
   */
  def timeSource: TimeSource = {
    if (_timeSource == null) {
      _timeSource = SystemClock
    }
    _timeSource
  }

  def timeSource_=(value: TimeSource) {
    _timeSource = value
  }

  /**今日の日付を所得する。
   *
   * 日付は、あらかじめ設定済みの [[jp.tricreo.baseunits.scala.time.TimeZone]] に基づき計算する。
   * `TimeZone`を未設定の状態でこのメソッドを呼び出してはならない。
   *
   * @return 今日の日付
   * @throws IllegalStateException [[jp.tricreo.baseunits.scala.time.TimeZone]]が未設定の場合
   */
  def today: CalendarDate = {
    if (defaultTimeZone == null) {
      throw new IllegalStateException("CalendarDate cannot be computed without setting a default TimeZone.")
    }
    now.calendarDate(defaultTimeZone)
  }

}