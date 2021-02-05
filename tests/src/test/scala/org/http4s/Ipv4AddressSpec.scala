/*
 * Copyright 2013 http4s.org
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

package org.http4s

import cats.kernel.laws.discipline.{HashTests, OrderTests}
import org.http4s.Uri.Ipv4Address
import org.http4s.laws.discipline.HttpCodecTests
import org.http4s.util.Renderer.renderString

class Ipv4AddressSpec extends Http4sSpec {
  checkAll("Order[Ipv4Address]", OrderTests[Ipv4Address].order)
  checkAll("Hash[Ipv4Address]", HashTests[Ipv4Address].hash)
  checkAll("HttpCodec[Ipv4Address]", HttpCodecTests[Ipv4Address].httpCodec)

  "render" should {
    "render all 4 octets" in {
      renderString(ipv4"192.168.0.1") must_== "192.168.0.1"
    }
  }

  "fromInet4Address" should {
    "round trip with toInet4Address" in prop { (ipv4: Ipv4Address) =>
      Ipv4Address.fromInet4Address(ipv4.toInet4Address) must_== ipv4
    }
  }

  "fromByteArray" should {
    "round trip with toByteArray" in prop { (ipv4: Ipv4Address) =>
      Ipv4Address.fromByteArray(ipv4.toByteArray) must beRight(ipv4)
    }
  }

  "compare" should {
    "be consistent with ip4s" in prop { (xs: List[Ipv4Address]) =>
      xs.sorted.map(_.address) must_== xs.map(_.address).sorted
    }

    "be consistent with Ordered" in prop { (a: Ipv4Address, b: Ipv4Address) =>
      math.signum(a.compareTo(b)) must_== math.signum(a.compare(b))
    }
  }

  "ipv4 interpolator" should {
    "be consistent with fromString" in {
      Right(ipv4"127.0.0.1") must_== Ipv4Address.fromString("127.0.0.1")
      Right(ipv4"192.168.0.1") must_== Ipv4Address.fromString("192.168.0.1")
    }

    "reject invalid values" in {
      illTyped("""ipv4"256.0.0.0"""")
      true
    }
  }
}
