/**
 * Copyright (c) 2022 eHealth Suisse
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ch.admin.bag.vaccination.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class VaccinesToTargetDiseasesConfigTest {

  @Autowired
  private VaccinesToTargetDiseasesConfig config;

  @Test
  public void testConfig() {
    assertThat(config.getVaccineSystem()).isEqualTo("http://fhir.ch/ig/ch-vacd/CodeSystem-ch-vacd-swissmedic-cs.html");
    assertThat(config.getTargetDiseaseSystem())
        .isEqualTo("http://fhir.ch/ig/ch-vacd/ValueSet/ch-vacd-targetdiseasesandillnessesundergoneforimmunization-vs");
  }

  @Test
  public void testVaccines() {
    assertThat(config.getVaccines().size()).isEqualTo(87);
    assertThat(config.getVaccines().get(0).getTarget().size()).isEqualTo(1);
    assertThat(config.getVaccines().get(0).getTarget().get(0).getCode()).isEqualTo("16901001");
    assertThat(config.getVaccines().get(0).getVaccine("").getCode()).isEqualTo("683");
    assertThat(config.getVaccines().get(0).getVaccine("vaccineSystem").getSystem()).isEqualTo("vaccineSystem");
    assertThat(config.getVaccines().get(1).getVaccine("").getCode()).isEqualTo("450");
    assertThat(config.getVaccines().get(0).getTarget().get(0).getName())
        .isEqualTo("Central European encephalitis (disorder)");
    assertThat(config.getVaccines().get(1).getTarget().get(0).getName())
        .isEqualTo("Central European encephalitis (disorder)");
    config.getVaccines().get(1).setTargetSystem("systemTarget");
    assertThat(config.getVaccines().get(1).getTarget().get(0).getSystem()).isEqualTo("systemTarget");
  }
}
