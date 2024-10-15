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
  image_profile: string;
  description: string;
}
