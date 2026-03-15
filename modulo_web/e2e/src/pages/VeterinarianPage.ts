import {type Locator, type Page} from '@playwright/test';

export class VeterinarianPage {
    readonly page: Page;
    readonly createButton: Locator;
    readonly vetRows: Locator;
    readonly nameInput: Locator;
    readonly emailInput: Locator;
    readonly passwordInput: Locator;
    readonly submitButton: Locator;

    constructor(page: Page) {
        this.page = page;
        this.createButton = page.getByTestId('create-vet-button');
        this.vetRows = page.getByTestId('vet-row');
        this.nameInput = page.getByTestId('name');
        this.emailInput = page.getByTestId('email');
        this.passwordInput = page.getByTestId('password');
        this.submitButton = page.getByTestId('vet-submit-button');
    }

    async goto() {
        await this.page.goto('/admin/veterinarians');
    }

    async createVeterinarian(name: string, email: string, password: string) {
        await this.createButton.click();
        await this.nameInput.fill(name);
        await this.emailInput.fill(email);
        await this.passwordInput.fill(password);
        await this.submitButton.click();
    }
}
