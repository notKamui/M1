#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include <iostream>





//////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char ** argv)
{
  // check arguments
  if(argc != 3){
    std::cout << "usage: " << argv[0] << " image1 imag2" << std::endl;
    return -1;
  }

  // load the first image
  std::cout << "load images ..." << std::endl;
  cv::Mat image1 = cv::imread(argv[1]);
  if(image1.empty()){
    std::cout << "error loading " << argv[1] << std::endl;
    return -1;
  }
  std::cout << "image1 size     : " << image1.cols << " x " << image1.rows << std::endl;
  std::cout << "image1 channels : " << image1.channels() << std::endl << std::endl;


  // load the second image
  cv::Mat image2 = cv::imread(argv[2],-1);
  if(image2.empty()){
    std::cout << "error loading " << argv[2] << std::endl;
    return -1;
  }
  std::cout << "image2 size : " << image2.cols << " x " << image2.rows << std::endl;
  std::cout << "image2 channels : " << image2.channels() << std::endl << std::endl;
  if(image2.channels() != 4){
    std::cout << "error loading, the second image should be RGBA" << std::endl;
    return -1;
  }
    

  // do something
  // ...


  // display an image
  cv::imshow("une image", image1);
  std::cout << "appuyer sur une touche ..." << std::endl;
  cv::waitKey();


  cv::imwrite("output/tp1ex4.jpg",image1);
  // save the image

  return 1;
}
