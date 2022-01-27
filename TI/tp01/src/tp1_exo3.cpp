#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>


//////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char **argv) {
    // check arguments
    if (argc != 2) {
        std::cout << "usage: " << argv[0] << " image" << std::endl;
        return -1;
    }

    // load the input image
    std::cout << "load image ..." << std::endl;
    cv::Mat image = cv::imread(argv[1]);
    if (image.empty()) {
        std::cout << "error loading " << argv[1] << std::endl;
        return -1;
    }
    std::cout << "image halfsize : " << image.cols << " x " << image.rows << std::endl;


    // do something
    // ...
//    for (int j = image.cols / 2 - 30; j < image.cols / 2 + 30; j++) {
//        for (int i = image.rows / 2 - 30; i < image.rows / 2 + 30; i++) {
//            image.at<cv::Vec3b>(i, j) = cv::Vec3b(0, 255, 0);
//        }
//    }

    int halfsize = 30;
    for (int j = image.cols / 2 - halfsize; j < image.cols / 2 + halfsize; j++) {
        for (int i = image.rows / 2 - halfsize; i < image.rows / 2 + halfsize; i++) {
            int c = ((j - (image.cols / 2. - halfsize)) + (i - (image.rows / 2. - halfsize))) / (halfsize * 4) * 255.;
            image.at<cv::Vec3b>(i, j) = cv::Vec3b(c, c, c);
        }
    }

    cv::circle(image, cv::Point(image.cols / 2, image.rows / 2), 20, cv::Scalar(0, 255, 255), -1);

    // display an image
    cv::imshow("une image", image);
    std::cout << "appuyer sur une touche ..." << std::endl;
    cv::waitKey();

    // save the image
    cv::imwrite("output/tp1ex3.jpg", image);

    return 1;
}
