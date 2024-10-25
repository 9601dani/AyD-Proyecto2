import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router'; 
import Swal from 'sweetalert2';
import { NavbarComponent } from '../../commons/navbar/navbar.component';
import { UserService } from '../../../services/user.service';
import { Attribute, Resources, ResponseString } from '../../../interfaces/interfaces';
import { ImgService } from '../../../services/img.service';

@Component({
  selector: 'app-add-resource',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NavbarComponent
  ],
  templateUrl: './add-resource.component.html',
  styleUrls: ['./add-resource.component.scss']
})
export class AddResourceComponent implements OnInit {
  resourceForm!: FormGroup;
  attributeSearch = new FormControl('');
  newAttributeNameControl = new FormControl('');
  newAttributeDescriptionControl = new FormControl('');
  selectedAttributesControl = new FormControl([]);
  isEditMode: boolean = false; 
  resourceId: number | null = null; 
  selectedFile: File | null = null;
  fileError: string | null = null;
  imagePreview: string | null = null;

  attributes: Array<{ id: number, name: string, description: string }> = [
    { id: 1, name: 'Número de Jugadores', description: '7 jugadores' }
  ];


  filteredAttributes: Array<{ id: number, name: string, description: string }> = [...this.attributes];
  addedAttributes: Array<{ id: number, name: string, description: string }> = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private _userService: UserService,
    private _imageService: ImgService
  ) { }

  ngOnInit(): void {
    this.resourceForm = this.fb.group({
      name: ['', Validators.required],
      attributes: ['']
    });

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.resourceId = +params['id'];
        this.loadResource(this.resourceId);
      }
    });

    this.getAllAttributes();

    this.attributeSearch.valueChanges.subscribe(value => {
      if (!value) {
        this.filteredAttributes = [...this.attributes];
        return;
      }
      this.filteredAttributes = this.attributes.filter(attr =>
        attr.name.toLowerCase().includes(value.toLowerCase())
      );
    });
  }

  getAllAttributes() {
    this._userService.getAllAttributes().subscribe((data: any) => {
      this.attributes = data;
      this.filteredAttributes = data;
    });

  }

  loadResource(id: number): void {
    this._userService.getResourceById(id).subscribe((data: Resources) =>{
      if (data) {
        this.resourceForm.get('name')?.setValue(data.name);
        this.addedAttributes = data.attributes;
        this.resourceForm.patchValue({ name: data.name });
        this.addedAttributes = data.attributes;
      }
    })

  }

  onSubmit(): void {
    if (this.resourceForm.valid && this.addedAttributes.length > 0) {
      const resourceData: Resources = {
        id:1,
        name: this.resourceForm.get('name')?.value,
        image: '',
        attributes: this.addedAttributes.map(attr => ({ id:attr.id, name: attr.name, description: attr.description }))
      };

      if (this.isEditMode && this.resourceId) {
        this._userService.updateResource(this.resourceId, resourceData).subscribe((data: Resources) => {
          if(data){
            Swal.fire({
              title: 'Recurso Actualizado',
              text: 'El recurso ha sido actualizado exitosamente',
              icon: 'success',
              confirmButtonText: 'Aceptar'
            });
            console.log('Recurso Actualizado:', resourceData);
          }
        });
      } else {
        if(!this.selectedFile){
          Swal.fire({
            title: 'Error',
            text: 'Por favor, selecciona una imagen',
            icon: 'error',
            confirmButtonText: 'Aceptar'
          });
          return;
        }
        this._imageService.saveImgResource(this.selectedFile).subscribe((data: ResponseString) => {
          if(data){
            resourceData.image = data.message;
            this._userService.createResource(resourceData).subscribe((data: any) => {
              if(data){
                Swal.fire({
                  title: 'Recurso Creado',
                  text: 'El recurso ha sido creado exitosamente',
                  icon: 'success',
                  confirmButtonText: 'Aceptar'
                });
                console.log('Recurso Creado:', resourceData);
              }
            });
          }
        });
      }
    } else {
      if(this.addedAttributes.length === 0){
        Swal.fire({
          title: 'Error',
          text: 'Por favor, agrega al menos un atributo',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      }
    }
  }

  addSelectedAttributes(): void {
    const selectedAttributeIds = this.selectedAttributesControl.value;
    if (!selectedAttributeIds) return;
    selectedAttributeIds.forEach((id: number) => {
      const attribute = this.attributes.find(attr => attr.id === id);
      if (attribute && !this.addedAttributes.find(attr => attr.id === id)) {
        this.addedAttributes.push(attribute);
      }
    });
    this.selectedAttributesControl.setValue([]);
  }

  removeAttribute(attrToRemove: { id: number, name: string, description: string }): void {
    this.addedAttributes = this.addedAttributes.filter(attr => attr.id !== attrToRemove.id);
  }

  addNewAttribute(): void {
    if(this.newAttributeNameControl.invalid || this.newAttributeDescriptionControl.invalid 
      || this.newAttributeNameControl.value === '' || this.newAttributeDescriptionControl.value === '')
    {
      Swal.fire({
        title: 'Error',
        text: 'Los campos de nombre de atributo y descripción de atributo son requeridos',
        icon: 'error',
        confirmButtonText: 'Aceptar'
        });
      return;
    } else{
      const newAttrName = this.newAttributeNameControl.value;
      const newAttrDescription = this.newAttributeDescriptionControl.value;
      if (newAttrName && newAttrDescription) {

        const newAttr = { id: this.attributes.length + 1, name: newAttrName, description: newAttrDescription };

        this._userService.createAttribute(newAttr).subscribe((data: any) => {
          if(data){
            this.attributes.push(data);
            this.filteredAttributes = this.attributes;
            this.newAttributeNameControl.setValue('');
            this.newAttributeDescriptionControl.setValue('');
            Swal.fire({
              title: 'Atributo Creado',
              text: 'El atributo ha sido creado exitosamente',
              icon: 'success',
              confirmButtonText: 'Aceptar'
            });
          }
        },(error: any) => {
          Swal.fire({
            title: 'Error',
            text: 'No se pudo guardar el atributo ',
            icon: 'error',
            confirmButtonText: 'Aceptar'
          });
        });
        
      }
    }
  }

  onCancel(): void {
    this.resourceForm.reset();
    this.addedAttributes = [];
    this.imagePreview = null;

  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (file.type.startsWith('image/')) {
        this.selectedFile = file;
        this.fileError = null;
        const reader = new FileReader();

        reader.onload = () => {
          this.imagePreview = reader.result as string;
        };

        reader.readAsDataURL(file);
      } else {
        this.fileError = 'Solo se permiten archivos de imagen.';
      }
    }
  }
}
