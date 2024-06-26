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
package ch.admin.bag.vaccination.controller;

import static org.assertj.core.api.Assertions.assertThat;

import ch.admin.bag.vaccination.config.ProfileConfig;
import ch.fhir.epr.adapter.data.dto.AuthorDTO;
import ch.fhir.epr.adapter.data.dto.HumanNameDTO;
import ch.fhir.epr.adapter.data.dto.VaccinationDTO;
import ch.fhir.epr.adapter.data.dto.VaccinationRecordDTO;
import ch.fhir.epr.adapter.data.dto.ValueDTO;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class VaccinationRecordControllerTest {
  @LocalServerPort
  private int port;
  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private ProfileConfig profileConfig;

  @BeforeEach
  void before() {
    profileConfig.setLocalMode(true);
    profileConfig.setHuskyLocalMode(null);
  }

  @Test
  void createVaccinationRecordPdf_noExceptionOccurs() throws Exception {
    HumanNameDTO performer = new HumanNameDTO("Victor", "Frankenstein2", "Dr.", null, null);

    ValueDTO vaccineCode = new ValueDTO("123456789", "123456789", "testsystem");
    ValueDTO status = new ValueDTO("completed", "completed", "testsystem");
    VaccinationDTO vaccinationDTO = new VaccinationDTO(null, vaccineCode,
        Arrays.asList(new ValueDTO("777", "myDisease", "mySystem")), null, 3,
        LocalDate.now(), performer, null, "lotNumber", null, status);

    List<VaccinationDTO> vaccinationDTOs = Arrays.asList(vaccinationDTO);

    VaccinationRecordDTO vaccinationRecordDTO =
        new VaccinationRecordDTO(new AuthorDTO(performer), performer, Collections.emptyList(), Collections.emptyList(),
            vaccinationDTOs, Collections.emptyList());
    vaccinationRecordDTO.setI18nTargetDiseases(Arrays.asList());
    HttpEntity<VaccinationRecordDTO> request = new HttpEntity<>(vaccinationRecordDTO);

    assertThat(request.getBody().getVaccinations().size()).isEqualTo(1);
    ResponseEntity<Resource> response =
        restTemplate.postForEntity("http://localhost:" + port + "/vaccinationRecord/exportToPDF",
            request, Resource.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    Resource resource = response.getBody();
    InputStream stream = resource.getInputStream();

    File pdfFile = new File("exportToPDF.pdf");

    Files.copy(stream, pdfFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

    IOUtils.closeQuietly(stream);
  }

  @Test
  void getPatientName_noInput_returnPatientName() throws Exception {
    String response = restTemplate.getForObject("http://localhost:" + port
        + "/vaccinationRecord/communityIdentifier/GAZELLE/oid/1.3.6.1.4.1.21367.13.20.3000/localId/IHEBLUE-2599/name",
        String.class);

    assertThat(response).isEqualTo("Max Mustermann");
  }
}
