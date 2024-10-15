import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'image',
  standalone: true
})
export class ImagePipe implements PipeTransform {

  bucket: string = 'https://storage.googleapis.com/bucket_ayd1/';

  transform(value: string): string {
    return this.bucket + value;
  }

}
