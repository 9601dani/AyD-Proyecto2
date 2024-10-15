export interface CompanySetting {
    id: number;
    keyName: string;
    keyValue: string;
    labelValue: string;
    isRequired: boolean;
    valueType: string;
    settingType: string;
    help: string;
}