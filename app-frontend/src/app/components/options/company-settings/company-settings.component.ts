import { Component, inject, OnInit } from '@angular/core';
import { NavbarComponent } from "../../commons/navbar/navbar.component";
import { UserService } from '../../../services/user.service';
import { CommonModule } from '@angular/common';
import { FormArray, FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import Swal from 'sweetalert2';
import { ImagePipe } from '../../../pipes/image.pipe';
import { CompanySetting } from '../../../models/CompanySetting.model';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip'; 
import { ImgService } from '../../../services/img.service';

@Component({
	selector: 'app-company-settings',
	standalone: true,
	imports: [CommonModule,
		NavbarComponent,
		ReactiveFormsModule,
		ImagePipe,
		MatIconModule,
		MatTooltipModule
	],
	templateUrl: './company-settings.component.html',
	styleUrl: './company-settings.component.scss'
})
export class CompanySettingsComponent implements OnInit {

	fb = inject(NonNullableFormBuilder);
	settingTypes: string[] = [];
	currentTab: string = "general";
	form!: FormGroup;
	settings: CompanySetting[] = [];
	originalImages: any[] = [];
	constructor(
		private _userService: UserService,
		private _imgService: ImgService
	) { }

	ngOnInit(): void {
		this.form = this.fb.group({});
		this.getSettingTypes();
	}

	getSettingTypes() {
		this._userService.getSettingsType().subscribe({
			next: response => {
				this.settingTypes = response;
				if (this.settingTypes.length > 0) {
					this.currentTab = this.settingTypes[0];
					this.getCompanySettings(this.currentTab);
				}
			},
			error: err => {
				console.error(err.message);
			}
		})
	}

	getCompanySettings(settingType: string) {
		this._userService.getCompanySettingsByType(settingType).subscribe({
			next: (response: CompanySetting[]) => {
				this.form = this.fb.group({});
				this.settings = response;
				response.forEach(companySetting => {
					const control = new FormControl();
					control.setValue(companySetting.keyValue);
					control.addValidators(companySetting.isRequired ? Validators.required : []);
					this.form.addControl(companySetting.keyName, control);
				})
			}
		})
	}

	change(settingType: string) {
		this.currentTab = settingType;
		this.getCompanySettings(this.currentTab);
	}

	onSubmit() {
		if (this.form.invalid) {
			Swal.fire({
				icon: "error",
				title: "Error",
				text: "Por favor, llene todos los campos",
			});
			return;
		}

		const formValues = this.form.value;
		const resultArray = Object.entries(formValues).map(([keyName, keyValue], index) => {
			return { keyName, keyValue, valueType: this.settings[index].valueType };
		});
		
		const images = resultArray.filter(r => r.valueType === 'image' && r.keyValue instanceof File);
		let data = resultArray.filter(r => r.valueType !== 'image');

		if(images.length > 0) {
			this.sendImages(images, data);
			return;
		}
		this.updateCompanySettings(data);
	}

	onFileSelected(event: Event, rowIndex: number, key: string) {
		const input = event.target as HTMLInputElement;
		if (input.files && input.files.length > 0) {
			const file = input.files[0];
			// this.editProfileForm.get("image")?.setValue(file);
			const value = this.form.get(key)?.getRawValue();
			this.form.get(key)?.setValue(file);
			const previewUrl = URL.createObjectURL(file);
			this.settings[rowIndex].keyValue = previewUrl;
			if(this.originalImages.filter(m => m.key === key).length === 0) {
				this.originalImages.push({key, value})
			}
		}
	}

	deleteImage(rowIndex: number, key: string) {
		this.settings[rowIndex].keyValue = this.originalImages.filter(m => m.key === key)[0].value;
	}

	compareFn(a: any, b: any): number {
		return 0;
	}


	sendImages(images: any[], data: any[]) {
		const formData = new FormData();
		images.forEach(i => {
			formData.append('files', i.keyValue as File);
		})
		
		this._imgService.upload(formData).subscribe({
			next: response => {
				response.forEach((path, index) => {
					data.push({keyName: images[index].keyName, keyValue: path, valueType: images[index].valueType})
				})
				this.updateCompanySettings(data);
			},
			error: error => {
				Swal.fire({
					title: 'Error!',
					text: error.error.message,
					icon: 'error',
					timer: 2000,
					showConfirmButton: false
				})
			}
		})
	}

	updateCompanySettings(data: any[]) {
		this._userService.updateCompanySettings(data).subscribe({
			next: response => {
				Swal.fire({
					title: "Éxito!",
					text: 'Datos actualizados con éxito!',
					icon: 'success',
					timer: 2000,
					showConfirmButton: false
				})
				window.location.reload();
			}, error: err => {
				Swal.fire({
					title: 'Error!',
					text: err.error.message,
					icon: 'error',
					timer: 2000,
					showConfirmButton: false
				})
			}
		})
	}

}
