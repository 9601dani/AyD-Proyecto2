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
