/*
 * Copyright 2011 Tricreo Inc and the Others.
 * lastModified : 2011/04/21
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
package jp.tricreo.baseunits.scala.money

import collection.Iterator
import collection.mutable.ListBuffer

/**
 * 割り当ての集合。
 *
 * @tparam T 割り当て対象
 * @param allotment 割り当ての要素（単一）
 */
class MoneyFan[T]
(private val allotments: Set[Allotment[T]])
  extends Iterable[Allotment[T]] {

  def this() = this (Set.empty[Allotment[T]])

  def this(allotment: Allotment[T]) = this (Set(allotment))

  def iterator: Iterator[Allotment[T]] = allotments.iterator

  /**{@link MoneyFan}が保持する {@link Allotment}のうち、割り当て対象が {@code anEntity}であるものを返す。
   *
   * @param anEntity 割り当て対象
   * @return {@link Allotment}。見つからなかった場合は{@code null}
   */
  def allotment(anEntity: T): Option[Allotment[T]] =
    allotments.find(_.entity == anEntity)

  override def hashCode: Int = allotments.hashCode

  override def equals(obj: Any): Boolean = obj match {
    case that: MoneyFan[T] => allotments == that.allotments
    case _ => false
  }


  /**この{@link MoneyFan}から{@code subtracted}を引いた差を返す。
   *
   * @param subtracted {@link MoneyFan}
   * @return {@link MoneyFan}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def minus(subtracted: MoneyFan[T]) = plus(subtracted.negated)

  /**
   * この {@link MoneyFan}の {@link Allotment}を {@link Allotment#negated()}した {@link Set}で構成される
   * 新しい {@link MoneyFan}を返す。
   *
   * @return {@link MoneyFan}
   */
  def negated = {
    var negatedAllotments = Set.empty[Allotment[T]]
    for (allotment <- allotments) {
      negatedAllotments += (allotment.negated)
    }
    new MoneyFan[T](negatedAllotments)
  }

  /**
   * この{@link MoneyFan}に{@code added}を足した和を返す。
   *
   * <p>同じ割り当て対象に対する割当額は、マージする。また、割当額が0の {@link Allotment} は取り除く。</p>
   *
   * @param added {@link MoneyFan}
   * @return {@link MoneyFan}
   * @throws IllegalArgumentException 引数に{@code null}を与えた場合
   */
  def plus(added: MoneyFan[T]) = {
    var allEntities = Set.empty[T]
    for (allotment <- allotments) {
      allEntities += (allotment.entity)
    }
    for (allotment <- added.allotments) {
      allEntities += (allotment.entity)
    }
    var summedAllotments = Set.empty[Allotment[T]]
    for (entity <- allEntities) {
      if (this.allotment(entity) == None) {
        summedAllotments += (added.allotment(entity).get)
      } else if (added.allotment(entity) == None) {
        summedAllotments += (this.allotment(entity).get)
      } else {
        val sum = this.allotment(entity).get.amount.plus(added.allotment(entity).get.amount);
        summedAllotments += (new Allotment[T](entity, sum))
      }
    }
    new MoneyFan[T](summedAllotments).withoutZeros
  }

  override def toString = allotments.toString

  /**全ての割り当ての合計額を返す。
   *
   * @return 合計額
   */
  def total = asTally.net

  private def asTally = {
    val moneies = ListBuffer.empty[Money]
    for (allotment <- allotments) {
      moneies += (allotment.amount)
    }
    new Tally(moneies.toList)
  }

  /**
   * このインスタンスが保持する {@link Allotment} のうち、割り当て金額が{@code 0}であるものを取り除いた
   * 新しい {@link MoneyFan}を返す。
   *
   * @return {@link MoneyFan}
   */
  private def withoutZeros = {
    var nonZeroAllotments = Set.empty[Allotment[T]]
    for (allotment <- allotments) {
      if (allotment.breachEncapsulationOfAmount.isZero == false) {
        nonZeroAllotments += (allotment)
      }
    }
    new MoneyFan[T](nonZeroAllotments)
  }
}

object MoneyFan {

  def apply[T](allotments: Set[Allotment[T]]): MoneyFan[T] = new MoneyFan[T](allotments)

  def apply[T](allotment: Allotment[T]): MoneyFan[T] = new MoneyFan[T](allotment)

  def apply[T]: MoneyFan[T] = new MoneyFan[T]

  def unapply[T](moneyFan: MoneyFan[T]) = Some(moneyFan.allotments)

}