﻿/**
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
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { BehaviorSubject, combineLatest, Subscription } from 'rxjs';
import { IAdverseEvent, IInfectiousDiseases, IMedicalProblem, IVaccination } from '../../../model';
import { IHumanDTO, MapperService, TableWrapperComponent, trackLangChange } from '../../../shared';
import { SharedDataService } from '../../../shared/services/shared-data.service';
import { SpinnerService } from '../../../shared/services/spinner.service';
import { SharedComponentModule } from '../../../shared/shared-component.module';
import { SharedLibsModule } from '../../../shared/shared-libs.module';
import { VaccinationListComponent } from '../../vaccination/main-components';
import { VaccinationRecordService } from '../service/vaccination-record.service';
import { PatientActionComponent } from '../../common/patient-action/patient-action.component';
import { initializeActionData } from '../../../shared/function';
import { BreakpointObserver } from '@angular/cdk/layout';
import { SessionInfoService } from '../../../core/security/session-info.service';

@Component({
  selector: 'vm-vaccination-record',
  standalone: true,
  imports: [SharedLibsModule, TableWrapperComponent, VaccinationListComponent, SharedComponentModule, PatientActionComponent],
  templateUrl: './vaccination-record.component.html',
  styleUrls: ['./vaccination-record.component.scss'],
})
export class VaccinationRecordComponent implements OnInit, OnDestroy {
  router = inject(Router);
  vaccinations!: MatTableDataSource<IVaccination>;
  illnesses!: MatTableDataSource<IInfectiousDiseases>;
  allergies!: MatTableDataSource<IAdverseEvent>;
  medicalProblems!: MatTableDataSource<IMedicalProblem>;
  mapper: MapperService = inject(MapperService);
  vaccinationRecordService = inject(VaccinationRecordService);
  record$ = combineLatest([this.vaccinationRecordService.queryOneRecord(), trackLangChange()]);
  allergyColumns: string[] = ['occurrenceDate', 'code', 'recorder'];
  illnessesColumns: string[] = ['recordedDate', 'illnessCode', 'recorder'];
  vaccinationColumns: string[] = ['occurrenceDate', 'targetDiseases', 'vaccineCode', 'doseNumber', 'recorder'];
  medicalProblemColumns: string[] = ['recordedDate', 'medicalProblemCode', 'clinicalStatus', 'recorder'];
  subscription!: Subscription;
  sharedDataService: SharedDataService = inject(SharedDataService);
  spinnerService: SpinnerService = inject(SpinnerService);
  public getScreenWidth: any;
  patientRender: boolean = true;
  isMobile: boolean = false;
  sessionInfoService: SessionInfoService = inject(SessionInfoService);
  patient: BehaviorSubject<IHumanDTO> = new BehaviorSubject<IHumanDTO>({} as IHumanDTO);

  constructor(private breakPointObserver: BreakpointObserver) {
    this.getScreenWidth = window.innerWidth;
    this.breakPointObserver.observe(['(max-width: 780px)']).subscribe(result => {
      this.isMobile = result.matches;
    });
  }

  ngOnInit(): void {
    this.getRecord();
    initializeActionData('record', this.sharedDataService);
  }

  getRecord(): Subscription {
    this.spinnerService.show();
    if (this.getScreenWidth < 780) {
      this.patientRender = false;
    }
    return (this.subscription = this.vaccinationRecordService.queryOneRecord().subscribe({
      next: value => {
        this.sharedDataService.setSessionStorage();
        this.record$.subscribe(([]) => {
          this.allergies = new MatTableDataSource<IAdverseEvent>(this.mapper.allergyTranslateMapper(value.allergies));
          this.vaccinations = new MatTableDataSource<IVaccination>(this.mapper.vaccinationTranslateMapper(value.vaccinations));
          this.illnesses = new MatTableDataSource<IInfectiousDiseases>(this.mapper.illnessesTranslateMapper(value.pastIllnesses));
          this.medicalProblems = new MatTableDataSource<IMedicalProblem>(this.mapper.problemTranslateMapper(value.medicalProblems));
        });
        this.patient.next(value.patient);
        this.spinnerService.hide();
      },
    }));
  }

  navigateToAllergy(row: IAdverseEvent): void {
    this.router.navigate(['allergy', row.id, 'detail']);
  }

  navigateToIllness(row: IInfectiousDiseases): void {
    this.router.navigate(['infectious-diseases', row.id, 'detail']);
  }

  navigateToVaccination(row: IVaccination): void {
    this.router.navigate(['vaccination', row.id, 'detail']);
  }

  navigateToMedicalProblem(row: IMedicalProblem): void {
    this.router.navigate(['medical-problem', row.id, 'detail']);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  addVaccination(): void {
    this.router.navigateByUrl('vaccination/new');
  }

  addMedicalProblem(): void {
    // this.router.navigate(['medical-problem', 'new']);
    this.router.navigateByUrl('medical-problem/new');
  }

  addIllness(): void {
    this.router.navigateByUrl('infectious-diseases/new');
  }

  addAllergy(): void {
    this.router.navigateByUrl('allergy/new');
  }
}
