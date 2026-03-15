import {type Locator, type Page} from '@playwright/test';

export class WorkstationPage {
    readonly page: Page;
    readonly createButton: Locator;
    readonly workstationRows: Locator;
    readonly descInput: Locator;
    readonly submitButton: Locator;

    constructor(page: Page) {
        this.page = page;
        this.createButton = page.getByTestId('create-workstation-button');
        this.workstationRows = page.getByTestId('workstation-row');
        this.descInput = page.getByTestId('desc');
        this.submitButton = page.getByTestId('workstation-submit-button');
    }

    async goto() {
        await this.page.goto('/admin/workstations');
    }

    async createWorkstation(description: string) {
        await this.createButton.click();
        await this.descInput.fill(description);
        await this.submitButton.click();
    }
}
