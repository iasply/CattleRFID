import {type Locator, type Page} from '@playwright/test';

export class VaccinePage {
    readonly page: Page;
    readonly vaccineRows: Locator;
    readonly animalSelect: Locator;
    readonly vaccineTypeInput: Locator;
    readonly weightInput: Locator;
    readonly vaccinationDateInput: Locator;
    readonly vetSelect: Locator;
    readonly submitButton: Locator;

    constructor(page: Page) {
        this.page = page;
        this.vaccineRows = page.getByTestId('vaccine-row');
        this.animalSelect = page.getByTestId('vaccine-animal-select');
        this.vaccineTypeInput = page.getByTestId('vaccine_type');
        this.weightInput = page.getByTestId('current_weight');
        this.vaccinationDateInput = page.getByTestId('vaccination_date');
        this.vetSelect = page.getByTestId('vaccine-vet-select');
        this.submitButton = page.getByTestId('vaccine-submit-button');
    }

    async goto() {
        await this.page.goto('/admin/vaccines');
    }

    async createVaccine(animalTag: string, type: string, weight: string, vetUsername: string) {
        await this.page.goto('/admin/vaccines/create');
        await this.animalSelect.selectOption(animalTag);
        await this.vaccineTypeInput.fill(type);
        await this.weightInput.fill(weight);
        // Date is usually pre-filled, but can be set if needed
        await this.vetSelect.selectOption(vetUsername);
        await this.submitButton.click();
    }
}
