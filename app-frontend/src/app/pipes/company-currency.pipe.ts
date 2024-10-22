import { inject, Pipe, PipeTransform } from '@angular/core';
import { LocalStorageService } from '../services/local-storage.service';

@Pipe({
  name: 'companyCurrency',
  standalone: true
})
export class CompanyCurrencyPipe implements PipeTransform {

  _localStorageService = inject(LocalStorageService);

  transform(value: string): string {
    const currency = this._localStorageService.getCurrency() || "$";
    return `${currency} ${value}`;
  }

}
