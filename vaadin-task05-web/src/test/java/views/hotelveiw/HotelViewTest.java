package views.hotelveiw;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import ui.AbstractUITest;
import views.hotelveiw.vo.HotelData;

import java.util.*;

public class HotelViewTest extends AbstractUITest {
    @Test
    public void testAddHotel() throws InterruptedException {
        driver.get(BASE_URL);
        Thread.sleep(1000); // Let the user actually see something!

        // test need at least one category - create Category If No Any Exist
        createCategoryIfNoAnyExist();

        // go to hotel page
        WebElement hotelMenu = (new WebDriverWait(driver, 300))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                        "//span[@class='v-menubar-menuitem v-menubar-menuitem-unchecked'][./span/@class='v-menubar-menuitem-caption'][contains(./span/text(), 'Hotel')]"
                )));
        hotelMenu.click();
        Thread.sleep(1000); // Let the user actually see something!

        WebElement addHotelBtnHotelView = (new WebDriverWait(driver, 300))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("addHotelBtnHotelView_id")));
        Assert.assertEquals("http://localhost:8080/hotel", driver.getCurrentUrl());
        Thread.sleep(1000); // Let the user actually see something!

        // determine data for saving Hotels
        Set<HotelData> hotelDataSet = new LinkedHashSet<>();
        int quantityOfHotelsCreation = 3;
        int minHotelRating = 0;
        int maxHotelRating = 5;
        for (int i = 0; i < quantityOfHotelsCreation; i++) {
            // create HotelData for save
            Integer rating =  minHotelRating + (int)(Math.random() * (maxHotelRating + 1));
            HotelData hotelData = new HotelData("SeleniumHotel_Name_" + (i + 1),
                    "SeleniumHotel_Address_" + (i + 1), "SeleniumHotel_URL_" + (i + 1),
                    "SeleniumHotel_Description_" + (i + 1), rating.toString() );
            // save HotelData in Set
            hotelDataSet.add(hotelData);
        }

        int countBeforeSaveHotels = getAllFilteredHotels("SeleniumHotel").size();
        Thread.sleep(3000); // Let the user actually see something!

        // add new Hotels from hotelDataSet
        for (HotelData hotelData : hotelDataSet) {
            addHotel(hotelData, addHotelBtnHotelView);
        }

        int countAfterSaveHotels = getAllFilteredHotels("SeleniumHotel").size();

        Assert.assertEquals("Where saved not 3 hotels with name contains [SeleniumHotel]",
                3, countAfterSaveHotels - countBeforeSaveHotels);
        Thread.sleep(3000); // Let the user actually see something!
    }

    private void addHotel(HotelData hotelData, WebElement addHotelBtnHotelView) throws InterruptedException {
        addHotelBtnHotelView.click();
        // set Hotel Name
        WebElement nameTextFieldHotelEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("nameTextFieldHotelEditForm_id")));
        if (nameTextFieldHotelEditForm.isEnabled()) {
            nameTextFieldHotelEditForm.clear();
            nameTextFieldHotelEditForm.sendKeys(hotelData.getHotelName());
        }
        Thread.sleep(1000); // Let the user actually see something!

        // set Hotel address
        WebElement addressTextFieldHotelEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("addressTextFieldHotelEditForm_id")));
        if (addressTextFieldHotelEditForm.isEnabled()) {
            addressTextFieldHotelEditForm.clear();
            addressTextFieldHotelEditForm.sendKeys(hotelData.getHotelAddress());
        }
        Thread.sleep(1000); // Let the user actually see something!

        // set Hotel rating
        WebElement ratingTextFieldHotelEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("ratingTextFieldHotelEditForm_id")));
        if (ratingTextFieldHotelEditForm.isEnabled()) {
            ratingTextFieldHotelEditForm.clear();
            ratingTextFieldHotelEditForm.sendKeys(hotelData.getHotelRaiting());
        }
        Thread.sleep(1000); // Let the user actually see something!

        // set Hotel paymentMethod (Credit card 12%) - Cash by Default
        WebElement paymentMethodFieldHotelEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("paymentMethodFieldHotelEditForm_id")));
        if (paymentMethodFieldHotelEditForm.isEnabled()) {
            WebElement paymentMethodFieldRadioButtonGroupHotelEditForm = (new WebDriverWait(driver, 100))
                    .until(ExpectedConditions.presenceOfElementLocated(By.id("paymentMethodFieldRadioButtonGroupHotelEditForm_id")));
            if (paymentMethodFieldRadioButtonGroupHotelEditForm.isEnabled()) {
                List<WebElement> radioButtons = paymentMethodFieldRadioButtonGroupHotelEditForm.findElements(By.xpath(".//span[contains(@class, v-radiobutton)]"));
                for (WebElement radioButton : radioButtons) {
                    WebElement label = radioButton.findElement(By.xpath(".//label"));
                    String labelText = null;
                    if (label.isEnabled()) {
                        labelText = label.getText();
                        if ("Credit Card".equalsIgnoreCase(labelText)) {
                            label.click();
                            WebElement paymentMethodFieldGuarantyDepositHotelEditForm = (new WebDriverWait(driver, 100))
                                    .until(ExpectedConditions.presenceOfElementLocated(By.id("paymentMethodFieldGuarantyDepositHotelEditForm_id")));
                            if (paymentMethodFieldGuarantyDepositHotelEditForm.isEnabled()) {
                                paymentMethodFieldGuarantyDepositHotelEditForm.clear();
                                paymentMethodFieldGuarantyDepositHotelEditForm.sendKeys("12");
                            }
                        }
                    }
                }
            }
        }
        Thread.sleep(1000); // Let the user actually see something!

        // set Hotel categoryNativeSelect
        WebElement categoryNativeSelectHotelEditForm = driver.findElement(By.id("categoryNativeSelectHotelEditForm_id"));
        if (categoryNativeSelectHotelEditForm.isEnabled()) {
            Select categoryNativeSelect = new Select(categoryNativeSelectHotelEditForm.findElement(By.xpath(".//select")));
            // select Hostel category
            List<WebElement> categories = categoryNativeSelect.getOptions();
            int categoryQuantity = categories.isEmpty() ? 0 : (categories.size() - 1);
            int randomIndex = 1 + (int) (Math.random() * categoryQuantity);
            categoryNativeSelect.selectByIndex(randomIndex);
        }


        // set Hotel URL
        WebElement urlTextFieldHotelEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("urlTextFieldHotelEditForm_id")));
        if (urlTextFieldHotelEditForm.isEnabled()) {
            urlTextFieldHotelEditForm.clear();
            urlTextFieldHotelEditForm.sendKeys(hotelData.getHotelURL());
        }
        Thread.sleep(1000); // Let the user actually see something!

        // set Hotel Description
        WebElement descriptionTextAreaHotelEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("descriptionTextAreaHotelEditForm_id")));
        if (descriptionTextAreaHotelEditForm.isEnabled()) {
            descriptionTextAreaHotelEditForm.clear();
            descriptionTextAreaHotelEditForm.sendKeys(hotelData.getHotelDescription());
        }
        Thread.sleep(1000); // Let the user actually see something!

        WebElement saveHotelBtnHotelEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("saveHotelBtnHotelEditForm_id")));
        if (saveHotelBtnHotelEditForm.isEnabled()) {
            saveHotelBtnHotelEditForm.click();
        }

        boolean isSaveBtnOn = true;
        do {
            // wait wile saving in DB
            try {
                if (saveHotelBtnHotelEditForm.isDisplayed()) {
                    System.out.println("Wait, please!!! Is Saving data in DB. Save Btn is -> ON!");
                }
            } catch (StaleElementReferenceException exp) {
                isSaveBtnOn = false;
            }
        } while (isSaveBtnOn);
        Thread.sleep(1000); // Let the user actually see something!
    }

    private List<WebElement> getAllFilteredHotels(String filterByName) throws InterruptedException {
        // find All Hotels where name contains filterByName
        WebElement filterByNameTextFieldHotelView = driver.findElement(By.id("filterByNameTextFieldHotelView_id"));
        if (filterByNameTextFieldHotelView.isEnabled()) {
            filterByNameTextFieldHotelView.clear();
            filterByNameTextFieldHotelView.sendKeys(filterByName);
        }
        Thread.sleep(1000); // Let the user actually see something!

        return driver.findElements(By.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
    }

    private void createCategoryIfNoAnyExist() throws InterruptedException {
        // go to category page
        WebElement categoryMenu = (new WebDriverWait(driver, 300))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(
                        "//span[@class='v-menubar-menuitem v-menubar-menuitem-unchecked'][./span/@class='v-menubar-menuitem-caption'][contains(./span/text(), 'Category')]"
                )));
        categoryMenu.click();
        Thread.sleep(1000); // Let the user actually see something!

        // check what this is category page
        WebElement addCategoryBtnCategoryView = (new WebDriverWait(driver, 300))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("addCategoryBtnCategoryView_id")));
        Assert.assertEquals("http://localhost:8080/category", driver.getCurrentUrl());
        Thread.sleep(1000); // Let the user actually see something!

        // show All filtered Categories
        List<WebElement> allCategories = getAllFilteredCategories("");
        Thread.sleep(1000); // Let the user actually see something!

        if (allCategories.isEmpty()) {
            // create category if no any exist
            addCategory("_Test_Selenium_Category", addCategoryBtnCategoryView);
        }
    }

    private List<WebElement> getAllFilteredCategories(String filterByName) throws InterruptedException {
        // find All Categories where name contains filterByName
        WebElement filterByNameTextFieldCategoryView = driver.findElement(By.id("filterByNameTextFieldCategoryView_id"));
        if (filterByNameTextFieldCategoryView.isEnabled()) {
            filterByNameTextFieldCategoryView.clear();
            filterByNameTextFieldCategoryView.sendKeys(filterByName);
        }
        Thread.sleep(1000); // Let the user actually see something!

        return driver.findElements(By.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
    }

    private void addCategory(String categoryName, WebElement addCategoryBtnCategoryView)
            throws InterruptedException {
        addCategoryBtnCategoryView.click();
        WebElement nameTextFieldCategoryEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("nameTextFieldCategoryEditForm_id")));
        if (nameTextFieldCategoryEditForm.isEnabled()) {
            nameTextFieldCategoryEditForm.clear();
            nameTextFieldCategoryEditForm.sendKeys(categoryName);
        }
        Thread.sleep(1000); // Let the user actually see something!

        WebElement saveCategoryBtnCategoryEditForm = (new WebDriverWait(driver, 100))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("saveCategoryBtnCategoryEditForm_id")));

        if (saveCategoryBtnCategoryEditForm.isEnabled()) {
            saveCategoryBtnCategoryEditForm.click();
        }
        boolean isSaveBtnOn = true;
        do {
            // wait wile saving in DB
            try {
                if (saveCategoryBtnCategoryEditForm.isDisplayed()) {
                    System.out.println("Wait, please!!! Is Saving data in DB. Save Btn is -> ON!");
                }
            } catch (StaleElementReferenceException exp) {
                isSaveBtnOn = false;
            }
        } while (isSaveBtnOn);
        Thread.sleep(1000); // Let the user actually see something!
    }
}
