export interface User {
  id:number,
  email:string,
  username:string
}

export interface UserProfile {
  id:number,
  nit:string,
  image_profile:string,
  description:string
}

export interface UserAllResponse {
  email: string;
  username: string;
  nit: string;
  imageProfile: string;
  description: string;
  dpi: string;
  phoneNumber: string;
  is2FA: boolean
}

export interface ResponseString {
  message: string;
}

export interface RequestString {
  message: string;
}


export interface Service{
  id:number;
  name:string;
  description:string;
  price:number;
  pageInformation:string;
  timeAprox:number;
  isAvailable:number;
}

export interface Roles{
  id:number;
  name:string;
  description:string;
}

export interface Employee{
  id:number;
  firstName:string;
  lastName:string;
  dateOfBirth:string;
  email:string;
  username:string;
  password:string;
  role:number;
}

export interface EmployeeWithImage {
  id:number;
  firstName:string;
  lastName:string;
  dateOfBirth:string;
  email:string;
  username:string;
  imageProfile:string;
  roles:string[];
}

export interface Attribute {
  id: number;
  name: string;
  description: string;
}

export interface Resources{
  id:number;
  name:string;
  image:string;
  attributes:Attribute[];
}

export interface ServiceWithEmplAndRes {
  name:string;
  description:string;
  price:number;
  pageInformation:string;
  timeAprox:number;
  isAvailable:number;
  employees:Employee[];
  resources:Resources[];
}

export interface CompanySettingConfig {
  keyName: string;
  property: string;
}

export interface Comment{
  id:number;
  FK_User:number;
  comment:string;
  value:number;
  created_at:string;
}

export interface CommentResponse{
  id:number;
  username:string;
  comment:string;
  value:number;
  createdAt:string;
}