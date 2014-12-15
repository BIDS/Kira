/*
 * Copyright (c) 2014. Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.berkeley.bids.kira.serialization

import com.esotericsoftware.kryo.Kryo
import com.twitter.chill.avro.AvroSerializer
import edu.berkeley.bids.kira.avro.FitsValue
import org.apache.spark.serializer.KryoRegistrator
import scala.reflect.ClassTag

class MaddKryoRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[FitsValue], AvroSerializer.SpecificRecordSerializer[FitsValue])
  }
}
