import {type Locator, type Page} from '@playwright/test';

export class CattlePage {
    readonly page: Page;
    readonly createLink: Locator;
    readonly cattleRows: Locator;
    readonly nameInput: Locator;
    readonly weightInput: Locator;
    readonly submitButton: Locator;

    constructor(page: Page) {
        this.page = page;
        this.createLink = page.getByTestId('create-cattle-link');
        this.cattleRows = page.getByTestId('cattle-row');
        this.nameInput = page.getByTestId('name');
        this.weightInput = page.getByTestId('weight');
        this.submitButton = page.getByTestId('cattle-submit-button');
    }

    async goto() {
        await this.page.goto('/admin/cattle');
    }

    async createCattle(name: string, weight: string) {
        await this.createLink.click();
        await this.nameInput.fill(name);
        await this.weightInput.fill(weight);
        await this.submitButton.click();
    }
}
