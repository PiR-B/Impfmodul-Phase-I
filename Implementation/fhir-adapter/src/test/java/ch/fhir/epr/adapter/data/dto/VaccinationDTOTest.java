/**
 * Copyright (c) 2023 eHealth Suisse
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
package ch.fhir.epr.adapter.data.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class VaccinationDTOTest {

  @Test
  void getDateOfDay_noInput_returnOccurrenceDate() {
    VaccinationDTO dto = new VaccinationDTO();
    LocalDate now = LocalDate.now();
    dto.setOccurrenceDate(now);

    assertEquals(now, dto.getDateOfEvent());
  }

  @SuppressWarnings("deprecation")
  @Test
  void getIllnessCode_noInput_returnCode() {
    VaccinationDTO dto = new VaccinationDTO();
    ValueDTO code = mock(ValueDTO.class);
    dto.setCode(code);

    assertEquals(code, dto.getVaccineCode());
  }
}
