import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router'; 
import Swal from 'sweetalert2';
import { NavbarComponent } from '../../commons/navbar/navbar.component';

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

  attributes: Array<{ id: number, name: string, description: string }> = [
    { id: 1, name: 'Número de Jugadores', description: '7 jugadores' },
    { id: 2, name: 'Número de Jugadores', description: '11 jugadores' },
    { id: 3, name: 'Material', description: 'Madera' },
    { id: 4, name: 'Resistencia', description: 'Alta' },
    { id: 5, name: 'Peso', description: '10 kg' }
  ];

  filteredAttributes: Array<{ id: number, name: string, description: string }> = [...this.attributes];
  addedAttributes: Array<{ id: number, name: string, description: string }> = [];

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute
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

  loadResource(id: number): void {
    const resource = {
      id: id,
      name: 'Cancha de Futbol 5',
      attributes: [
        { id: 1, name: 'Tamaño', description: '50 x 30 metros' },
        { id: 2, name: 'Material', description: 'Césped Artificial' }
      ]
    };

    this.resourceForm.patchValue({ name: resource.name });
    this.addedAttributes = resource.attributes;
  }

  onSubmit(): void {
    if (this.resourceForm.valid && this.addedAttributes.length > 0) {
      const resourceData = {
        name: this.resourceForm.get('name')?.value,
        attributes: this.addedAttributes.map(attr => ({ name: attr.name, description: attr.description }))
      };

      if (this.isEditMode) {
        Swal.fire({
          title: 'Recurso Actualizado',
          text: 'El recurso ha sido actualizado exitosamente',
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
        console.log('Recurso Actualizado:', resourceData);
      } else {
        Swal.fire({
          title: 'Recurso Guardado',
          text: 'El recurso ha sido guardado exitosamente',
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
        console.log('Recurso Guardado:', resourceData);
      }
    } else {
      Swal.fire({
        title: 'Error',
        text: 'Por favor, llene todos los campos, recuerda que debes agregar al menos un atributo',
        icon: 'error',
        confirmButtonText: 'Aceptar'
      });
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
    console.log("CLICK")
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
        this.attributes.push(newAttr);
        this.filteredAttributes.push(newAttr);
        this.addedAttributes.push(newAttr);
        this.newAttributeNameControl.setValue('');
        this.newAttributeDescriptionControl.setValue('');
      }
    }
  }

  onCancel(): void {
    this.resourceForm.reset();
    this.addedAttributes = [];
  }
}
