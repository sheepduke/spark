/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.streaming

import java.{util => ju}

import scala.collection.JavaConverters._

import org.apache.spark.annotation.Experimental
import org.apache.spark.sql.streaming.StreamingQueryStatus.indent

/**
 * :: Experimental ::
 * Status and metrics of a streaming Source.
 *
 * @param description Description of the source corresponding to this status.
 * @param offsetDesc Description of the current offset if known.
 * @param inputRate Current rate (rows/sec) at which data is being generated by the source.
 * @param processingRate Current rate (rows/sec) at which the query is processing data from
 *                       the source.
 * @param triggerDetails Low-level details of the currently active trigger (e.g. number of
 *                      rows processed in trigger, latency of intermediate steps, etc.).
 *                      If no trigger is active, then it will have details of the last completed
 *                      trigger.
 * @since 2.0.0
 */
@Experimental
class SourceStatus private(
    val description: String,
    val offsetDesc: String,
    val inputRate: Double,
    val processingRate: Double,
    val triggerDetails: ju.Map[String, String]) {

  override def toString: String =
    "SourceStatus:" + indent(prettyString)

  private[sql] def prettyString: String = {
    val triggerDetailsLines =
      triggerDetails.asScala.map { case (k, v) => s"$k: $v" }
    s"""$description
       |Available offset: $offsetDesc
       |Input rate: $inputRate rows/sec
       |Processing rate: $processingRate rows/sec
       |Trigger details:
       |""".stripMargin + indent(triggerDetailsLines)

  }
}

/** Companion object, primarily for creating SourceStatus instances internally */
private[sql] object SourceStatus {
  def apply(
      desc: String,
      offsetDesc: String,
      inputRate: Double,
      processingRate: Double,
      triggerDetails: Map[String, String]): SourceStatus = {
    new SourceStatus(desc, offsetDesc, inputRate, processingRate, triggerDetails.asJava)
  }
}
